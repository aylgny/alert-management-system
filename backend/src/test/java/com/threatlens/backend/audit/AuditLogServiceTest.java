package com.threatlens.backend.audit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuditLogServiceTest {

    @Mock
    private AuditLogRepository auditLogRepository;

    @InjectMocks
    private AuditLogService auditLogService;

    @Test
    void shouldReturnAllAuditLogsOrderedByTimestampDesc() {
        AuditLog log1 = new AuditLog(
                "admin@threatlens.com",
                "MANUAL_IP_BLOCK",
                "91.44.12.8",
                "SUCCESS",
                "Manual block created"
        );

        AuditLog log2 = new AuditLog(
                "firewall-engine",
                "SQL_INJECTION_BLOCKED",
                "45.88.12.90",
                "BLOCKED",
                "SQL injection blocked"
        );

        when(auditLogRepository.findAllByOrderByTimestampDesc())
                .thenReturn(List.of(log2, log1));

        List<AuditLog> result = auditLogService.getAllAuditLogs();

        assertEquals(2, result.size());
        assertEquals("firewall-engine", result.get(0).getActor());
        assertEquals("SQL_INJECTION_BLOCKED", result.get(0).getAction());
        assertEquals("admin@threatlens.com", result.get(1).getActor());
        assertEquals("MANUAL_IP_BLOCK", result.get(1).getAction());

        verify(auditLogRepository, times(1)).findAllByOrderByTimestampDesc();
    }

    @Test
    void shouldCreateAuditLog() {
        when(auditLogRepository.save(any(AuditLog.class)))
                .thenAnswer(invocation -> {
                    AuditLog auditLog = invocation.getArgument(0);
                    auditLog.setId(1L);
                    return auditLog;
                });

        AuditLog result = auditLogService.log(
                "firewall-engine",
                "XSS_BLOCKED",
                "203.0.113.55",
                "BLOCKED",
                "XSS payload detected"
        );

        assertEquals(1L, result.getId());
        assertEquals("firewall-engine", result.getActor());
        assertEquals("XSS_BLOCKED", result.getAction());
        assertEquals("203.0.113.55", result.getTarget());
        assertEquals("BLOCKED", result.getResult());
        assertEquals("XSS payload detected", result.getDetails());
        assertNotNull(result.getTimestamp());

        verify(auditLogRepository, times(1)).save(any(AuditLog.class));
    }
}