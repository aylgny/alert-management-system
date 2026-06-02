package com.threatlens.backend.policy;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class PolicySeeder implements CommandLineRunner {

    private final PolicyService policyService;

    public PolicySeeder(PolicyService policyService) {
        this.policyService = policyService;
    }

    @Override
    public void run(String... args) {
        policyService.getRuleByEventType("SQL_INJECTION");
        policyService.getRuleByEventType("XSS_ATTEMPT");
        policyService.getRuleByEventType("ADMIN_LOGIN");
    }
}