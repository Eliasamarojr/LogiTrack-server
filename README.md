# LogiTrack — API (Spring Boot)

API **REST JSON** do LogiTrack: autenticação, veículos, viagens, manutenção e agregados para o dashboard. Este repositório concentra **regras de negócio**, **persistência** (JPA/Hibernate + MySQL) e **segurança**; o cliente web está no projeto **LogiTrack-web** (Angular).

---

## Escopo e premissas

- Validação definitiva, autorização e modelo relacional vivem **aqui**; o front apenas consome contratos estáveis.
- Após `POST /auth/login`, a API devolve o JWT em **`Set-Cookie`** com **HttpOnly**, não como segredo persistido pelo front em `localStorage`. O filtro de segurança lê o token **do cookie** (e, secundariamente, de `Authorization: Bearer`, útil para clientes não-browser ou ferramentas).
- Ambiente local típico: API em `http://localhost:8080` e SPA em `http://localhost:4200` — **origens diferentes**; CORS com **`allowCredentials: true`** no servidor e **`withCredentials: true`** no cliente são obrigatórios para o cookie ser enviado.
- Em desenvolvimento, o esquema MySQL evolui com **`spring.jpa.hibernate.ddl-auto: update`**. Para produção séria, o ideal é migrar para **Flyway/Liquibase** ou SQL versionado; até lá, documente alterações de modelo neste repositório.

---

## Ambiente de desenvolvimento

### Requisitos

| Ferramenta | Notas |
|------------|--------|
| **JDK 17** | Alinhado ao `pom.xml` (parent Spring Boot 3.2.x). |
| **Maven 3.9+** | Build e empacotamento (`mvn`). |
| **MySQL** | Base `logitrack` (ou URL JDBC equivalente); utilizador com permissões de DDL se usar `ddl-auto: update`. |

### Variáveis e ficheiro de configuração

Os valores padrão estão em `src/main/resources/application.yml`. Sobrescreva com variáveis de ambiente quando fizer sentido:

| Variável | Efeito |
|----------|--------|
| `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD` | Ligação JDBC. |
| `PORT` | Porta HTTP (defeito **8080**). |
| `LOGITRACK_JWT_SECRET` | Segredo de assinatura do JWT — **obrigatoriamente longo e aleatório em produção**. |
| `LOGITRACK_COOKIE_SECURE` | `true` em HTTPS. |
| `LOGITRACK_COOKIE_SAME_SITE` | `Lax`, `Strict` ou `None` (com `Secure`) conforme o arranjo de domínios front/API. |
| `LOGITRACK_FRONTEND_URL` | Origem pública do SPA (ex. `https://app.exemplo.com`); acrescentada às patterns CORS. |

### Instalação e execução

```bash
cd LogiTrack-server
mvn spring-boot:run
```

Com jar já construído:

```bash
mvn -DskipTests package
java -jar target/logitrack-server-0.0.1-SNAPSHOT.jar
```

A API fica em **http://localhost:8080** (ou no `PORT` definido). O endpoint de readiness simples é **`GET /actuator/health`** (configurado como permitido sem autenticação).

### Utilizador inicial (seed)

Em ambiente de desenvolvimento, o projeto inclui seed com credenciais configuráveis em `logitrack.seed` no `application.yml` (por defeito `admin` / `admin123`). **Altere ou desative em produção.**

### Docker

```bash
docker build -t logitrack-server .
docker run --rm -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://host.docker.internal:3306/logitrack?createDatabaseIfNotExist=true \
  -e SPRING_DATASOURCE_USERNAME=... \
  -e SPRING_DATASOURCE_PASSWORD=... \
  -e LOGITRACK_JWT_SECRET=... \
  logitrack-server
```

Ajuste o host MySQL (`host.docker.internal`, rede Docker, ou serviço gestionado) conforme o teu ambiente.

### Testes

```bash
mvn test
```

---

## Arquitetura do backend

### Organização do código

| Pacote / área | Responsabilidade |
|----------------|------------------|
| `controller/` | Endpoints HTTP, DTOs de entrada/saída, `ResponseCookie` em autenticação. |
| `service/` | Orquestração e regras de aplicação. |
| `repository/` | Spring Data JPA, queries JPQL e **native queries** onde agregados o beneficiam. |
| `domain/` | Entidades JPA (`Vehicle`, `Travel`, `Maintenance`, …). |
| `config/` | Segurança, CORS, propriedades (`Jwt*`, `Cors*`). |
| `security/` | Filtro JWT, codificação de passwords, serviço de token. |
| `exception/` | Tratamento global de erros (`@ControllerAdvice`). |

### Stack e decisões principais

- **Spring Boot 3.2** com **Java 17**: ecossistema maduro para APIs e integração JPA/Segurança.
- **Spring Security** em modo **stateless** (sem sessão no servidor): cada pedido autenticável traz o JWT; escala horizontal simples desde que o segredo e os relógios estejam alinhados.
- **JJWT** para criar e validar tokens com expiração configurável (`logitrack.jwt.expiration-ms`).
- **BCrypt** para _hash_ de passwords — armazenamento irreversível e comparável de forma segura.
- **Bean Validation** nos DTOs de entrada (`jakarta.validation`).
- **Actuator** com `health` exposto para orquestradores e probes, sem expor detalhes sensíveis.

### JWT em cookie HttpOnly (alinhado ao LogiTrack-web)

- O login define um cookie com **`httpOnly(true)`**, o que **impede leitura do token via JavaScript** no browser e reduz o impacto de falhas de XSS em comparação com guardar o JWT em `localStorage` ou variáveis globais. **Não elimina** a necessidade de sanitização, CSP e boas práticas de front — apenas retira o token das APIs legíveis por script.
- **`Secure`** e **`SameSite`** são configuráveis: em produção HTTPS, ative `LOGITRACK_COOKIE_SECURE=true` e escolha `SameSite` coerente com o facto de front e API partilharem ou não o mesmo _site_ (cross-subdomínio pode exigir `None` + `Secure`).
- **CSRF** está desligado na configuração atual (`csrf().disable()`), padrão comum em APIs puramente JSON + JWT em cabeçalho/cookie **quando** o cliente não é um formulário browser clássico; se no futuro expuser fluxos cookie-only com mutações sensíveis a partir de HTML third-party, reavalie **CSRF** ou estratégia de token duplo.
- O filtro aceita também **`Authorization: Bearer`**, o que facilita **testes** e **clientes não-browser** sem alterar o fluxo principal do SPA baseado em cookies.

### Dados: JPA, agregações e `LEFT JOIN`

- Relações **`@ManyToOne(..., fetch = LAZY)`** em entidades como `Travel` e `Maintenance` evitam carregar grafos inteiros por defeito e reduzem N+1 quando as consultas são bem delimitadas.
- Consultas de **dashboard** em `TravelRepository` usam **SQL nativo** com **`LEFT JOIN`** entre `vehicle` e `travel`: isso garante que **veículos sem viagens** ainda apareçam com soma **0** e contagem coerente — um `INNER JOIN` excluiria esses veículos. Agregar por `GROUP BY` no próprio SQL mantém o trabalho pesado no **MySQL** e devolve linhas já resumidas para o serviço/DTO, em vez de carregar todas as viagens para memória.

---

## Contrato com o front-end

Garanta que as origens do Angular são aceites em `logitrack.cors` (patterns `localhost` já incluídas para desenvolvimento). O SPA deve usar **`withCredentials: true`** nos pedidos à API, como descrito no README do **LogiTrack-web**.

---

## Referências

- [Spring Boot](https://spring.io/projects/spring-boot)
- [Spring Security](https://spring.io/projects/spring-security)
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
