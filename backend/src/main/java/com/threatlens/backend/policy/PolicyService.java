package com.threatlens.backend.policy;

import com.threatlens.backend.audit.AuditLogService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PolicyService {

    private final PolicyRuleRepository policyRuleRepository;
    private final AuditLogService auditLogService;

    public PolicyService(
            PolicyRuleRepository policyRuleRepository,
            AuditLogService auditLogService
    ) {
        this.policyRuleRepository = policyRuleRepository;
        this.auditLogService = auditLogService;
    }

    public List<PolicyRule> getAllRules() {
        return policyRuleRepository.findAll();
    }

    public PolicyRule getRuleByEventType(String eventType) {
        return policyRuleRepository.findByEventType(eventType)
                .orElseGet(() -> createDefaultRule(eventType));
    }

    public PolicyRule updateRule(String eventType, PolicyUpdateRequest request, String actor) {
        PolicyRule rule = getRuleByEventType(eventType);

        PolicyMode oldMode = rule.getMode();

        if (request.getMode() != null) {
            rule.setMode(request.getMode());
        }

        if (request.getRiskScore() != null) {
            rule.setRiskScore(request.getRiskScore());
        }

        if (request.getDescription() != null) {
            rule.setDescription(request.getDescription());
        }

        PolicyRule updatedRule = policyRuleRepository.save(rule);

        auditLogService.log(
                actor,
                "UPDATE_POLICY_RULE",
                eventType,
                "SUCCESS",
                "Policy mode changed from " + oldMode + " to " + updatedRule.getMode()
        );

        return updatedRule;
    }

    private PolicyRule createDefaultRule(String eventType) {
        PolicyRule rule;

        if ("SQL_INJECTION".equals(eventType)) {
            rule = new PolicyRule(
                    "SQL_INJECTION",
                    PolicyMode.BLOCK,
                    95,
                    "Detect SQL injection payloads in protected API traffic"
            );
        } else if ("XSS_ATTEMPT".equals(eventType)) {
            rule = new PolicyRule(
                    "XSS_ATTEMPT",
                    PolicyMode.BLOCK,
                    85,
                    "Detect script and browser-based injection payloads"
            );
        } else if ("ADMIN_LOGIN".equals(eventType)) {
            rule = new PolicyRule(
                    "ADMIN_LOGIN",
                    PolicyMode.DETECT_ONLY,
                    75,
                    "Detect suspicious admin login attempts"
            );
        } else {
            rule = new PolicyRule(
                    eventType,
                    PolicyMode.DETECT_ONLY,
                    50,
                    "Default detection rule"
            );
        }

        return policyRuleRepository.save(rule);
    }
}