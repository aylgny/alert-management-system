package com.threatlens.backend.policy;

import com.threatlens.backend.audit.AuditLogService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PolicyServiceTest {

    @Mock
    private PolicyRuleRepository policyRuleRepository;

    @Mock
    private AuditLogService auditLogService;

    @InjectMocks
    private PolicyService policyService;

    @Test
    void shouldReturnAllPolicyRules() {
        PolicyRule sqlRule = new PolicyRule(
                "SQL_INJECTION",
                PolicyMode.BLOCK,
                95,
                "Block SQL injection"
        );

        PolicyRule xssRule = new PolicyRule(
                "XSS_ATTEMPT",
                PolicyMode.BLOCK,
                85,
                "Block XSS attempts"
        );

        when(policyRuleRepository.findAll()).thenReturn(List.of(sqlRule, xssRule));

        List<PolicyRule> result = policyService.getAllRules();

        assertEquals(2, result.size());
        assertEquals("SQL_INJECTION", result.get(0).getEventType());
        assertEquals("XSS_ATTEMPT", result.get(1).getEventType());

        verify(policyRuleRepository, times(1)).findAll();
    }

    @Test
    void shouldReturnExistingRuleByEventType() {
        PolicyRule existingRule = new PolicyRule(
                "SQL_INJECTION",
                PolicyMode.BLOCK,
                95,
                "Block SQL injection"
        );

        when(policyRuleRepository.findByEventType("SQL_INJECTION"))
                .thenReturn(Optional.of(existingRule));

        PolicyRule result = policyService.getRuleByEventType("SQL_INJECTION");

        assertEquals("SQL_INJECTION", result.getEventType());
        assertEquals(PolicyMode.BLOCK, result.getMode());
        assertEquals(95, result.getRiskScore());

        verify(policyRuleRepository, times(1)).findByEventType("SQL_INJECTION");
        verify(policyRuleRepository, never()).save(any(PolicyRule.class));
    }

    @Test
    void shouldCreateDefaultSqlInjectionRuleWhenRuleDoesNotExist() {
        when(policyRuleRepository.findByEventType("SQL_INJECTION"))
                .thenReturn(Optional.empty());

        when(policyRuleRepository.save(any(PolicyRule.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        PolicyRule result = policyService.getRuleByEventType("SQL_INJECTION");

        assertEquals("SQL_INJECTION", result.getEventType());
        assertEquals(PolicyMode.BLOCK, result.getMode());
        assertEquals(95, result.getRiskScore());
        assertEquals(
                "Detect SQL injection payloads in protected API traffic",
                result.getDescription()
        );

        verify(policyRuleRepository, times(1)).save(any(PolicyRule.class));
    }

    @Test
    void shouldCreateDefaultXssRuleWhenRuleDoesNotExist() {
        when(policyRuleRepository.findByEventType("XSS_ATTEMPT"))
                .thenReturn(Optional.empty());

        when(policyRuleRepository.save(any(PolicyRule.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        PolicyRule result = policyService.getRuleByEventType("XSS_ATTEMPT");

        assertEquals("XSS_ATTEMPT", result.getEventType());
        assertEquals(PolicyMode.BLOCK, result.getMode());
        assertEquals(85, result.getRiskScore());
        assertEquals(
                "Detect script and browser-based injection payloads",
                result.getDescription()
        );

        verify(policyRuleRepository, times(1)).save(any(PolicyRule.class));
    }

    @Test
    void shouldCreateDefaultAdminLoginRuleWhenRuleDoesNotExist() {
        when(policyRuleRepository.findByEventType("ADMIN_LOGIN"))
                .thenReturn(Optional.empty());

        when(policyRuleRepository.save(any(PolicyRule.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        PolicyRule result = policyService.getRuleByEventType("ADMIN_LOGIN");

        assertEquals("ADMIN_LOGIN", result.getEventType());
        assertEquals(PolicyMode.DETECT_ONLY, result.getMode());
        assertEquals(75, result.getRiskScore());
        assertEquals(
                "Detect suspicious admin login attempts",
                result.getDescription()
        );

        verify(policyRuleRepository, times(1)).save(any(PolicyRule.class));
    }

    @Test
    void shouldUpdatePolicyRule() {
        PolicyRule existingRule = new PolicyRule(
                "SQL_INJECTION",
                PolicyMode.BLOCK,
                95,
                "Block SQL injection"
        );

        PolicyUpdateRequest request = new PolicyUpdateRequest();
        request.setMode(PolicyMode.DETECT_ONLY);
        request.setRiskScore(60);
        request.setDescription("Detect only during tuning");

        when(policyRuleRepository.findByEventType("SQL_INJECTION"))
                .thenReturn(Optional.of(existingRule));

        when(policyRuleRepository.save(any(PolicyRule.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        PolicyRule result = policyService.updateRule(
                "SQL_INJECTION",
                request,
                "admin@threatlens.com"
        );

        assertEquals("SQL_INJECTION", result.getEventType());
        assertEquals(PolicyMode.DETECT_ONLY, result.getMode());
        assertEquals(60, result.getRiskScore());
        assertEquals("Detect only during tuning", result.getDescription());

        verify(policyRuleRepository, times(1)).save(existingRule);
        verify(auditLogService, times(1)).log(
                eq("admin@threatlens.com"),
                eq("UPDATE_POLICY_RULE"),
                eq("SQL_INJECTION"),
                eq("SUCCESS"),
                eq("Policy mode changed from BLOCK to DETECT_ONLY")
        );
    }

    @Test
    void shouldKeepExistingValuesWhenUpdateRequestFieldsAreNull() {
        PolicyRule existingRule = new PolicyRule(
                "SQL_INJECTION",
                PolicyMode.BLOCK,
                95,
                "Block SQL injection"
        );

        PolicyUpdateRequest request = new PolicyUpdateRequest();

        when(policyRuleRepository.findByEventType("SQL_INJECTION"))
                .thenReturn(Optional.of(existingRule));

        when(policyRuleRepository.save(any(PolicyRule.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        PolicyRule result = policyService.updateRule(
                "SQL_INJECTION",
                request,
                "admin@threatlens.com"
        );

        assertEquals(PolicyMode.BLOCK, result.getMode());
        assertEquals(95, result.getRiskScore());
        assertEquals("Block SQL injection", result.getDescription());

        verify(policyRuleRepository, times(1)).save(existingRule);
        verify(auditLogService, times(1)).log(
                eq("admin@threatlens.com"),
                eq("UPDATE_POLICY_RULE"),
                eq("SQL_INJECTION"),
                eq("SUCCESS"),
                eq("Policy mode changed from BLOCK to BLOCK")
        );
    }
}