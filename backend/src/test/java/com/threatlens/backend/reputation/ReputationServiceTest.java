package com.threatlens.backend.reputation;

import com.threatlens.backend.alert.Alert;
import com.threatlens.backend.alert.AlertRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReputationServiceTest {

    @Mock
    private AlertRepository alertRepository;

    @InjectMocks
    private ReputationService reputationService;

    @Test
    void shouldMarkIpAsMaliciousWhenMaxRiskScoreIsAtLeast90() {
        Alert alert1 = createAlert("91.44.12.8", "SQL_INJECTION", 95);
        Alert alert2 = createAlert("91.44.12.8", "FAILED_LOGIN", 75);

        when(alertRepository.findAll()).thenReturn(List.of(alert1, alert2));

        List<ReputationEntry> result = reputationService.getSourceReputation();

        assertEquals(1, result.size());

        ReputationEntry entry = result.get(0);

        assertEquals("91.44.12.8", entry.getSourceIp());
        assertEquals(2L, entry.getEventCount());
        assertEquals(95, entry.getMaxRiskScore());
        assertEquals("MALICIOUS", entry.getReputationStatus());
        assertTrue(entry.getEventTypes().contains("SQL_INJECTION"));
        assertTrue(entry.getEventTypes().contains("FAILED_LOGIN"));
    }

    @Test
    void shouldMarkIpAsSuspiciousWhenMaxRiskScoreIsBetween70And89() {
        Alert alert = createAlert("203.0.113.55", "XSS_ATTEMPT", 85);

        when(alertRepository.findAll()).thenReturn(List.of(alert));

        List<ReputationEntry> result = reputationService.getSourceReputation();

        assertEquals(1, result.size());

        ReputationEntry entry = result.get(0);

        assertEquals("203.0.113.55", entry.getSourceIp());
        assertEquals(1L, entry.getEventCount());
        assertEquals(85, entry.getMaxRiskScore());
        assertEquals("SUSPICIOUS", entry.getReputationStatus());
        assertEquals(Set.of("XSS_ATTEMPT"), entry.getEventTypes());
    }

    @Test
    void shouldMarkIpAsMonitoredWhenMaxRiskScoreIsBelow70() {
        Alert alert = createAlert("8.8.8.8", "FAILED_LOGIN", 25);

        when(alertRepository.findAll()).thenReturn(List.of(alert));

        List<ReputationEntry> result = reputationService.getSourceReputation();

        assertEquals(1, result.size());

        ReputationEntry entry = result.get(0);

        assertEquals("8.8.8.8", entry.getSourceIp());
        assertEquals(1L, entry.getEventCount());
        assertEquals(25, entry.getMaxRiskScore());
        assertEquals("MONITORED", entry.getReputationStatus());
        assertEquals(Set.of("FAILED_LOGIN"), entry.getEventTypes());
    }

    @Test
    void shouldGroupAlertsBySourceIp() {
        Alert alert1 = createAlert("91.44.12.8", "SQL_INJECTION", 95);
        Alert alert2 = createAlert("91.44.12.8", "XSS_ATTEMPT", 85);
        Alert alert3 = createAlert("8.8.8.8", "FAILED_LOGIN", 25);

        when(alertRepository.findAll()).thenReturn(List.of(alert1, alert2, alert3));

        List<ReputationEntry> result = reputationService.getSourceReputation();

        assertEquals(2, result.size());

        ReputationEntry maliciousEntry = result.get(0);
        ReputationEntry monitoredEntry = result.get(1);

        assertEquals("91.44.12.8", maliciousEntry.getSourceIp());
        assertEquals(2L, maliciousEntry.getEventCount());
        assertEquals(95, maliciousEntry.getMaxRiskScore());
        assertEquals("MALICIOUS", maliciousEntry.getReputationStatus());

        assertEquals("8.8.8.8", monitoredEntry.getSourceIp());
        assertEquals(1L, monitoredEntry.getEventCount());
        assertEquals(25, monitoredEntry.getMaxRiskScore());
        assertEquals("MONITORED", monitoredEntry.getReputationStatus());
    }

    @Test
    void shouldIgnoreAlertsWithoutSourceIp() {
        Alert validAlert = createAlert("91.44.12.8", "SQL_INJECTION", 95);

        Alert alertWithoutSourceIp = new Alert();
        alertWithoutSourceIp.setSourceIp(null);
        alertWithoutSourceIp.setEventType("XSS_ATTEMPT");
        alertWithoutSourceIp.setRiskScore(85);

        Alert alertWithBlankSourceIp = new Alert();
        alertWithBlankSourceIp.setSourceIp("");
        alertWithBlankSourceIp.setEventType("FAILED_LOGIN");
        alertWithBlankSourceIp.setRiskScore(25);

        when(alertRepository.findAll()).thenReturn(
                List.of(validAlert, alertWithoutSourceIp, alertWithBlankSourceIp)
        );

        List<ReputationEntry> result = reputationService.getSourceReputation();

        assertEquals(1, result.size());
        assertEquals("91.44.12.8", result.get(0).getSourceIp());
    }

    private Alert createAlert(String sourceIp, String eventType, Integer riskScore) {
        Alert alert = new Alert();

        alert.setSourceIp(sourceIp);
        alert.setEventType(eventType);
        alert.setRiskScore(riskScore);

        return alert;
    }
}