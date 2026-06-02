package com.threatlens.backend.protectedapi;

import com.threatlens.backend.alert.Alert;
import com.threatlens.backend.alert.AlertService;
import com.threatlens.backend.audit.AuditLogService;
import com.threatlens.backend.mitigation.MitigationService;
import com.threatlens.backend.policy.PolicyMode;
import com.threatlens.backend.policy.PolicyRule;
import com.threatlens.backend.policy.PolicyService;
import org.springframework.stereotype.Service;

@Service
public class RequestInspectionService {

    private final MitigationService mitigationService;
    private final AlertService alertService;
    private final AuditLogService auditLogService;
    private final PolicyService policyService;

    public RequestInspectionService(
            MitigationService mitigationService,
            AlertService alertService,
            AuditLogService auditLogService,
            PolicyService policyService
    ) {
        this.mitigationService = mitigationService;
        this.alertService = alertService;
        this.auditLogService = auditLogService;
        this.policyService = policyService;
    }

    public FirewallResponse inspectLoginRequest(LoginRequest request) {
        String sourceIp = request.getSourceIp();
        String username = request.getUsername();
        String password = request.getPassword();

        if (mitigationService.isSourceIpBlocked(sourceIp)) {
            auditLogService.log(
                    "firewall-engine",
                    "ACTIVE_MITIGATION_BLOCKED",
                    sourceIp,
                    "BLOCKED",
                    "Protected login request blocked because source IP has an active mitigation."
            );

            return new FirewallResponse(
                    "BLOCKED",
                    "Request blocked by active mitigation",
                    sourceIp,
                    "ACTIVE_MITIGATION",
                    100
            );
        }

        String combinedPayload = safe(username) + " " + safe(password);

        if (containsSqlInjection(combinedPayload)) {
            return applyPolicyDecision(
                    sourceIp,
                    "SQL_INJECTION",
                    "SQL injection attempt detected",
                    "Suspicious SQL injection pattern detected in protected login request.",
                    combinedPayload
            );
        }

        if (containsXss(combinedPayload)) {
            return applyPolicyDecision(
                    sourceIp,
                    "XSS_ATTEMPT",
                    "XSS attempt detected",
                    "Suspicious script payload detected in protected login request.",
                    combinedPayload
            );
        }

        if (isSuspiciousAdminLogin(username, password)) {
            return applyPolicyDecision(
                    sourceIp,
                    "ADMIN_LOGIN",
                    "Suspicious admin login",
                    "An admin login attempt was detected on the protected API.",
                    combinedPayload
            );
        }

        auditLogService.log(
                "firewall-engine",
                "PROTECTED_REQUEST_ALLOWED",
                sourceIp,
                "ALLOWED",
                "Protected login request passed firewall checks."
        );

        return new FirewallResponse(
                "ALLOWED",
                "Request passed firewall checks",
                sourceIp,
                "NONE",
                0
        );
    }

    private FirewallResponse applyPolicyDecision(
            String sourceIp,
            String eventType,
            String title,
            String description,
            String rawMessage
    ) {
        PolicyRule policyRule = policyService.getRuleByEventType(eventType);
        PolicyMode mode = policyRule.getMode();
        Integer riskScore = policyRule.getRiskScore();

        if (riskScore == null) {
            riskScore = 50;
        }

        if (mode == PolicyMode.DISABLED) {
            auditLogService.log(
                    "firewall-engine",
                    eventType + "_DISABLED",
                    sourceIp,
                    "ALLOWED",
                    "Policy disabled. Request was allowed without alert. Payload: " + rawMessage
            );

            return new FirewallResponse(
                    "ALLOWED",
                    "Policy disabled for " + eventType,
                    sourceIp,
                    eventType,
                    riskScore
            );
        }

        createSecurityAlert(
                sourceIp,
                eventType,
                title,
                description,
                rawMessage,
                calculateSeverity(riskScore),
                riskScore
        );

        if (mode == PolicyMode.DETECT_ONLY) {
            auditLogService.log(
                    "firewall-engine",
                    eventType + "_DETECTED",
                    sourceIp,
                    "ALLOWED_WITH_ALERT",
                    "Policy mode is DETECT_ONLY. Alert created but request allowed. Payload: " + rawMessage
            );

            return new FirewallResponse(
                    "ALLOWED_WITH_ALERT",
                    eventType + " detected but allowed by policy",
                    sourceIp,
                    eventType,
                    riskScore
            );
        }

        auditLogService.log(
                "firewall-engine",
                eventType + "_BLOCKED",
                sourceIp,
                "BLOCKED",
                "Policy mode is BLOCK. Request blocked. Payload: " + rawMessage
        );

        return new FirewallResponse(
                "BLOCKED",
                eventType + " blocked by policy",
                sourceIp,
                eventType,
                riskScore
        );
    }

    private void createSecurityAlert(
            String sourceIp,
            String eventType,
            String title,
            String description,
            String rawMessage,
            String severity,
            Integer riskScore
    ) {
        Alert alert = new Alert();

        alert.setTitle(title);
        alert.setDescription(description);
        alert.setSourceIp(sourceIp);
        alert.setTargetIp("protected-api");
        alert.setEventType(eventType);
        alert.setSeverity(severity);
        alert.setStatus("NEW");
        alert.setRiskScore(riskScore);
        alert.setSourceSystem("Protected API Firewall");
        alert.setRawMessage(rawMessage);

        alertService.createAlert(alert);
    }

    private String calculateSeverity(Integer riskScore) {
        if (riskScore == null) {
            return "MEDIUM";
        }

        if (riskScore >= 90) {
            return "CRITICAL";
        }

        if (riskScore >= 70) {
            return "HIGH";
        }

        if (riskScore >= 40) {
            return "MEDIUM";
        }

        return "LOW";
    }

    private boolean containsSqlInjection(String payload) {
        String lower = payload.toLowerCase();

        return lower.contains("' or '1'='1")
                || lower.contains("\" or \"1\"=\"1")
                || lower.contains(" or 1=1")
                || lower.contains("union select")
                || lower.contains("drop table")
                || lower.contains("--")
                || lower.contains(";--");
    }

    private boolean containsXss(String payload) {
        String lower = payload.toLowerCase();

        return lower.contains("<script")
                || lower.contains("</script>")
                || lower.contains("javascript:")
                || lower.contains("onerror=")
                || lower.contains("onload=");
    }

    private boolean isSuspiciousAdminLogin(String username, String password) {
        if (username == null) {
            return false;
        }

        String lowerUsername = username.toLowerCase();

        return lowerUsername.contains("admin")
                && password != null
                && password.length() < 8;
    }

    private String safe(String value) {
        if (value == null) {
            return "";
        }

        return value;
    }
}