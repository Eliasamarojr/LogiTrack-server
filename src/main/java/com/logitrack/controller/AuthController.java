package com.logitrack.controller;

import com.logitrack.common.ApiResponse;
import com.logitrack.config.JwtCookieProperties;
import com.logitrack.config.JwtProperties;
import com.logitrack.dto.auth.LoginRequestDTO;
import com.logitrack.dto.auth.LoginResponseDTO;
import com.logitrack.dto.auth.LoginResult;
import com.logitrack.dto.auth.MeResponseDTO;
import com.logitrack.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.time.Duration;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtProperties jwtProperties;
    private final JwtCookieProperties cookieProperties;

    public AuthController(AuthService authService, JwtProperties jwtProperties, JwtCookieProperties cookieProperties) {
        this.authService = authService;
        this.jwtProperties = jwtProperties;
        this.cookieProperties = cookieProperties;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponseDTO>> login(@Valid @RequestBody LoginRequestDTO body) {
        LoginResult result = authService.login(body);
        ResponseCookie cookie = authCookie(result.accessToken());
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(ApiResponse.ok(new LoginResponseDTO(result.username()), "Autenticado"));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout() {
        ResponseCookie cleared = deleteAuthCookie();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cleared.toString())
                .body(ApiResponse.ok(null, "Sessão encerrada"));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<MeResponseDTO>> me(Principal principal) {
        return ResponseEntity.ok(ApiResponse.ok(new MeResponseDTO(principal.getName())));
    }

    private ResponseCookie authCookie(String token) {
        long maxAgeSeconds = Math.max(1L, jwtProperties.getExpirationMs() / 1000);
        return ResponseCookie.from(cookieProperties.getName(), token)
                .path(cookieProperties.getPath())
                .maxAge(Duration.ofSeconds(maxAgeSeconds))
                .httpOnly(true)
                .secure(cookieProperties.isSecure())
                .sameSite(cookieProperties.getSameSite())
                .build();
    }

    private ResponseCookie deleteAuthCookie() {
        return ResponseCookie.from(cookieProperties.getName(), "")
                .path(cookieProperties.getPath())
                .maxAge(0)
                .httpOnly(true)
                .secure(cookieProperties.isSecure())
                .sameSite(cookieProperties.getSameSite())
                .build();
    }
}
