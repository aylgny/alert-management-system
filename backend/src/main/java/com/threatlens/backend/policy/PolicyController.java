package com.threatlens.backend.policy;

import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/policies")
public class PolicyController {

    private final PolicyService policyService;

    public PolicyController(PolicyService policyService) {
        this.policyService = policyService;
    }

    @GetMapping
    public List<PolicyRule> getAllRules() {
        return policyService.getAllRules();
    }

    @PutMapping("/{eventType}")
    public PolicyRule updateRule(
            @PathVariable String eventType,
            @Valid @RequestBody PolicyUpdateRequest request,
            Authentication authentication
    ) {
        String actor = authentication.getName();

        return policyService.updateRule(eventType, request, actor);
    }
}