package com.threatlens.backend.config;

import com.threatlens.backend.alert.Alert;
import com.threatlens.backend.alert.AlertRepository;
import com.threatlens.backend.user.AppUser;
import com.threatlens.backend.user.AppUserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    private final AppUserRepository appUserRepository;
    private final AlertRepository alertRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(
            AppUserRepository appUserRepository,
            AlertRepository alertRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.appUserRepository = appUserRepository;
        this.alertRepository = alertRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        seedUsers();
        seedAlerts();
    }

    private void seedUsers() {
        if (appUserRepository.findByEmail("admin@threatlens.com").isEmpty()) {
            AppUser admin = new AppUser(
                    "ThreatLens Admin",
                    "admin@threatlens.com",
                    passwordEncoder.encode("Admin123!"),
                    "ADMIN"
            );

            appUserRepository.save(admin);
        }

        if (appUserRepository.findByEmail("analyst@threatlens.com").isEmpty()) {
            AppUser analyst = new AppUser(
                    "Security Analyst",
                    "analyst@threatlens.com",
                    passwordEncoder.encode("Analyst123!"),
                    "ANALYST"
            );

            appUserRepository.save(analyst);
        }
    }

    private void seedAlerts() {
        if (alertRepository.count() > 0) {
            return;
        }

        Alert alert1 = new Alert();
        alert1.setTitle("Multiple failed login attempts");
        alert1.setDescription("Several failed SSH login attempts detected from the same IP address.");
        alert1.setSourceIp("185.23.45.10");
        alert1.setTargetIp("10.0.0.5");
        alert1.setEventType("FAILED_LOGIN");
        alert1.setSeverity("HIGH");
        alert1.setStatus("NEW");
        alert1.setRiskScore(75);
        alert1.setSourceSystem("Linux Server");
        alert1.setRawMessage("Failed password for root from 185.23.45.10 port 55822 ssh2");

        Alert alert2 = new Alert();
        alert2.setTitle("Port scan detected");
        alert2.setDescription("A source IP attempted to connect to multiple ports in a short time.");
        alert2.setSourceIp("91.44.12.8");
        alert2.setTargetIp("10.0.0.8");
        alert2.setEventType("PORT_SCAN");
        alert2.setSeverity("CRITICAL");
        alert2.setStatus("NEW");
        alert2.setRiskScore(95);
        alert2.setSourceSystem("Network Sensor");
        alert2.setRawMessage("Multiple connection attempts detected across ports 22, 80, 443, 8080");

        Alert alert3 = new Alert();
        alert3.setTitle("Honeypot access detected");
        alert3.setDescription("An external IP tried to access a honeypot service.");
        alert3.setSourceIp("203.0.113.20");
        alert3.setTargetIp("10.0.0.15");
        alert3.setEventType("HONEYPOT_ACCESS");
        alert3.setSeverity("HIGH");
        alert3.setStatus("INVESTIGATING");
        alert3.setRiskScore(85);
        alert3.setSourceSystem("Honeypot");
        alert3.setRawMessage("External source attempted access to fake admin endpoint.");

        alertRepository.save(alert1);
        alertRepository.save(alert2);
        alertRepository.save(alert3);
    }
}