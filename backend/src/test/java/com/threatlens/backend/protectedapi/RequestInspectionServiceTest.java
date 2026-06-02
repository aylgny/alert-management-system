package com.threatlens.backend.protectedapi;

import com.threatlens.backend.alert.Alert;
import com.threatlens.backend.alert.AlertService;
import com.threatlens.backend.audit.AuditLogService;
import com.threatlens.backend.mitigation.MitigationService;
import com.threatlens.backend.policy.PolicyMode;
import com.threatlens.backend.policy.PolicyRule;
import com.threatlens.backend.policy.PolicyService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RequestInspectionServiceTest {

    @Mock
    private MitigationService mitigationService;

    @Mock
    private AlertService alertService;

    @Mock
    private AuditLogService auditLogService;

    @Mock
    private PolicyService policyService;

    @InjectMocks
    private RequestInspectionService requestInspectionService;

    @Test
    void shouldBlockSqlInjectionWhenPolicyModeIsBlock() {
        LoginRequest request = new LoginRequest();
        request.setSourceIp("91.44.12.8");
        request.setUsername("admin' OR '1'='1");
        request.setPassword("123456");

        PolicyRule sqlPolicy = new PolicyRule();
        sqlPolicy.setEventType("SQL_INJECTION");
        sqlPolicy.setMode(PolicyMode.BLOCK);
        sqlPolicy.setRiskScore(95);
        sqlPolicy.setDescription("Block SQL injection");

        when(mitigationService.isSourceIpBlocked("91.44.12.8")).thenReturn(false);
        when(policyService.getRuleByEventType("SQL_INJECTION")).thenReturn(sqlPolicy);

        FirewallResponse response = requestInspectionService.inspectLoginRequest(request);

        assertEquals("BLOCKED", response.getDecision());
        assertEquals("SQL_INJECTION", response.getEventType());
        assertEquals(95, response.getRiskScore());
        assertEquals("91.44.12.8", response.getSourceIp());

        verify(alertService, times(1)).createAlert(any(Alert.class));
        verify(auditLogService, times(1)).log(
                eq("firewall-engine"),
                eq("SQL_INJECTION_BLOCKED"),
                eq("91.44.12.8"),
                eq("BLOCKED"),
                anyString()
        );
    }

    @Test
    void shouldAllowWithAlertWhenSqlInjectionPolicyModeIsDetectOnly() {
        LoginRequest request = new LoginRequest();
        request.setSourceIp("91.44.12.8");
        request.setUsername("admin' OR '1'='1");
        request.setPassword("123456");

        PolicyRule sqlPolicy = new PolicyRule();
        sqlPolicy.setEventType("SQL_INJECTION");
        sqlPolicy.setMode(PolicyMode.DETECT_ONLY);
        sqlPolicy.setRiskScore(95);
        sqlPolicy.setDescription("Detect SQL injection only");

        when(mitigationService.isSourceIpBlocked("91.44.12.8")).thenReturn(false);
        when(policyService.getRuleByEventType("SQL_INJECTION")).thenReturn(sqlPolicy);

        FirewallResponse response = requestInspectionService.inspectLoginRequest(request);

        assertEquals("ALLOWED_WITH_ALERT", response.getDecision());
        assertEquals("SQL_INJECTION", response.getEventType());
        assertEquals(95, response.getRiskScore());

        verify(alertService, times(1)).createAlert(any(Alert.class));
        verify(auditLogService, times(1)).log(
                eq("firewall-engine"),
                eq("SQL_INJECTION_DETECTED"),
                eq("91.44.12.8"),
                eq("ALLOWED_WITH_ALERT"),
                anyString()
        );
    }

    @Test
    void shouldAllowWithoutAlertWhenSqlInjectionPolicyModeIsDisabled() {
        LoginRequest request = new LoginRequest();
        request.setSourceIp("91.44.12.8");
        request.setUsername("admin' OR '1'='1");
        request.setPassword("123456");

        PolicyRule sqlPolicy = new PolicyRule();
        sqlPolicy.setEventType("SQL_INJECTION");
        sqlPolicy.setMode(PolicyMode.DISABLED);
        sqlPolicy.setRiskScore(95);
        sqlPolicy.setDescription("SQL injection disabled");

        when(mitigationService.isSourceIpBlocked("91.44.12.8")).thenReturn(false);
        when(policyService.getRuleByEventType("SQL_INJECTION")).thenReturn(sqlPolicy);

        FirewallResponse response = requestInspectionService.inspectLoginRequest(request);

        assertEquals("ALLOWED", response.getDecision());
        assertEquals("SQL_INJECTION", response.getEventType());
        assertEquals(95, response.getRiskScore());

        verify(alertService, never()).createAlert(any(Alert.class));
        verify(auditLogService, times(1)).log(
                eq("firewall-engine"),
                eq("SQL_INJECTION_DISABLED"),
                eq("91.44.12.8"),
                eq("ALLOWED"),
                anyString()
        );
    }

    @Test
    void shouldBlockRequestWhenSourceIpHasActiveMitigation() {
        LoginRequest request = new LoginRequest();
        request.setSourceIp("45.88.12.90");
        request.setUsername("normaluser");
        request.setPassword("normalpass123");

        when(mitigationService.isSourceIpBlocked("45.88.12.90")).thenReturn(true);

        FirewallResponse response = requestInspectionService.inspectLoginRequest(request);

        assertEquals("BLOCKED", response.getDecision());
        assertEquals("ACTIVE_MITIGATION", response.getEventType());
        assertEquals(100, response.getRiskScore());
        assertEquals("45.88.12.90", response.getSourceIp());

        verify(alertService, never()).createAlert(any(Alert.class));
        verify(policyService, never()).getRuleByEventType(anyString());
        verify(auditLogService, times(1)).log(
                eq("firewall-engine"),
                eq("ACTIVE_MITIGATION_BLOCKED"),
                eq("45.88.12.90"),
                eq("BLOCKED"),
                anyString()
        );
    }

    @Test
    void shouldAllowNormalRequest() {
        LoginRequest request = new LoginRequest();
        request.setSourceIp("8.8.8.8");
        request.setUsername("aylin");
        request.setPassword("normalpass123");

        when(mitigationService.isSourceIpBlocked("8.8.8.8")).thenReturn(false);

        FirewallResponse response = requestInspectionService.inspectLoginRequest(request);

        assertEquals("ALLOWED", response.getDecision());
        assertEquals("NONE", response.getEventType());
        assertEquals(0, response.getRiskScore());
        assertEquals("8.8.8.8", response.getSourceIp());

        verify(alertService, never()).createAlert(any(Alert.class));
        verify(policyService, never()).getRuleByEventType(anyString());
        verify(auditLogService, times(1)).log(
                eq("firewall-engine"),
                eq("PROTECTED_REQUEST_ALLOWED"),
                eq("8.8.8.8"),
                eq("ALLOWED"),
                anyString()
        );
    }
}