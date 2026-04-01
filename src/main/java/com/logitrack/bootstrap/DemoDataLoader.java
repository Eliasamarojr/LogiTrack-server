package com.logitrack.bootstrap;

import com.logitrack.config.SeedProperties;
import com.logitrack.domain.Maintenance;
import com.logitrack.domain.MaintenanceStatus;
import com.logitrack.domain.Travel;
import com.logitrack.domain.UserAccount;
import com.logitrack.domain.Vehicle;
import com.logitrack.domain.VehicleType;
import com.logitrack.repository.MaintenanceRepository;
import com.logitrack.repository.TravelRepository;
import com.logitrack.repository.UserAccountRepository;
import com.logitrack.repository.VehicleRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
@Order(1)
public class DemoDataLoader implements ApplicationRunner {

    private final UserAccountRepository userAccountRepository;
    private final VehicleRepository vehicleRepository;
    private final TravelRepository travelRepository;
    private final MaintenanceRepository maintenanceRepository;
    private final PasswordEncoder passwordEncoder;
    private final SeedProperties seedProperties;

    public DemoDataLoader(
            UserAccountRepository userAccountRepository,
            VehicleRepository vehicleRepository,
            TravelRepository travelRepository,
            MaintenanceRepository maintenanceRepository,
            PasswordEncoder passwordEncoder,
            SeedProperties seedProperties) {
        this.userAccountRepository = userAccountRepository;
        this.vehicleRepository = vehicleRepository;
        this.travelRepository = travelRepository;
        this.maintenanceRepository = maintenanceRepository;
        this.passwordEncoder = passwordEncoder;
        this.seedProperties = seedProperties;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (userAccountRepository.existsByUsername(seedProperties.getAdminUsername())) {
            return;
        }
        UserAccount admin = new UserAccount();
        admin.setUsername(seedProperties.getAdminUsername());
        admin.setPasswordHash(passwordEncoder.encode(seedProperties.getAdminPassword()));
        userAccountRepository.save(admin);

        Vehicle leve = new Vehicle();
        leve.setPlate("ABC1D23");
        leve.setType(VehicleType.LEVE);
        leve.setModel("Fiorino");
        vehicleRepository.save(leve);

        Vehicle pesado = new Vehicle();
        pesado.setPlate("XYZ9K87");
        pesado.setType(VehicleType.PESADO);
        pesado.setModel("Accelo 1016");
        vehicleRepository.save(pesado);

        Travel t1 = new Travel();
        t1.setVehicle(leve);
        t1.setDepartureDateTime(LocalDateTime.now().minusDays(3));
        t1.setArrivalDateTime(LocalDateTime.now().minusDays(3).plusHours(4));
        t1.setOriginCity("Porto Alegre");
        t1.setDestinationCity("Pelotas");
        t1.setDistanceKm(new BigDecimal("280.5"));
        travelRepository.save(t1);

        Travel t2 = new Travel();
        t2.setVehicle(pesado);
        t2.setDepartureDateTime(LocalDateTime.now().minusDays(1));
        t2.setArrivalDateTime(LocalDateTime.now().minusDays(1).plusHours(6));
        t2.setOriginCity("Curitiba");
        t2.setDestinationCity("Joinville");
        t2.setDistanceKm(new BigDecimal("120"));
        travelRepository.save(t2);

        Maintenance m1 = new Maintenance();
        m1.setVehicle(leve);
        m1.setStartDate(LocalDate.now().plusDays(2));
        m1.setEndDate(LocalDate.now().plusDays(2));
        m1.setType("Preventiva");
        m1.setEstimatedCost(new BigDecimal("450.00"));
        m1.setStatus(MaintenanceStatus.SCHEDULED);
        maintenanceRepository.save(m1);

        Maintenance m2 = new Maintenance();
        m2.setVehicle(pesado);
        m2.setStartDate(LocalDate.now().plusDays(5));
        m2.setEndDate(LocalDate.now().plusDays(6));
        m2.setType("Revisão 20k km");
        m2.setEstimatedCost(new BigDecimal("2200.00"));
        m2.setStatus(MaintenanceStatus.SCHEDULED);
        maintenanceRepository.save(m2);

        Maintenance m3 = new Maintenance();
        m3.setVehicle(leve);
        m3.setStartDate(LocalDate.now().withDayOfMonth(10));
        m3.setType("Troca de óleo");
        m3.setEstimatedCost(new BigDecimal("320.00"));
        m3.setStatus(MaintenanceStatus.SCHEDULED);
        maintenanceRepository.save(m3);
    }
}
