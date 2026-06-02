package com.threatlens.backend.mitigation;

import com.threatlens.backend.audit.AuditLogService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MitigationServiceTest {

    @Mock
    private MitigationRepository mitigationRepository;

    @Mock
    private AuditLogService auditLogService;

    @InjectMocks
    private MitigationService mitigationService;

    @Test
    void shouldReturnAllMitigationsOrderedByCreatedAtDesc() {
        Mitigation mitigation1 = createMitigationEntity(
                1L,
                "91.44.12.8",
                "ACTIVE",
                300
        );

        Mitigation mitigation2 = createMitigationEntity(
                2L,
                "45.88.12.90",
                "RELEASED",
                300
        );

        when(mitigationRepository.findAllByOrderByCreatedAtDesc())
                .thenReturn(List.of(mitigation2, mitigation1));

        List<Mitigation> result = mitigationService.getAllMitigations();

        assertEquals(2, result.size());
        assertEquals("45.88.12.90", result.get(0).getSourceIp());
        assertEquals("91.44.12.8", result.get(1).getSourceIp());

        verify(mitigationRepository, times(1)).findAllByOrderByCreatedAtDesc();
    }

    @Test
    void shouldCreateMitigationWithValidTtl() {
        MitigationRequest request = new MitigationRequest();
        request.setSourceIp("91.44.12.8");
        request.setReason("Manual block test");
        request.setTtlSeconds(600);

        when(mitigationRepository.save(any(Mitigation.class)))
                .thenAnswer(invocation -> {
                    Mitigation mitigation = invocation.getArgument(0);
                    mitigation.setId(1L);
                    return mitigation;
                });

        Mitigation result = mitigationService.createMitigation(
                request,
                "admin@threatlens.com"
        );

        assertEquals(1L, result.getId());
        assertEquals("91.44.12.8", result.getSourceIp());
        assertEquals("Manual block test", result.getReason());
        assertEquals(600, result.getTtlSeconds());
        assertEquals("ACTIVE", result.getStatus());
        assertEquals("admin@threatlens.com", result.getCreatedBy());
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getExpiresAt());
        assertTrue(result.getExpiresAt().isAfter(result.getCreatedAt()));

        verify(mitigationRepository, times(1)).save(any(Mitigation.class));
        verify(auditLogService, times(1)).log(
                eq("admin@threatlens.com"),
                eq("MANUAL_IP_BLOCK"),
                eq("91.44.12.8"),
                eq("SUCCESS"),
                contains("Manual IP block created with TTL 600 seconds")
        );
    }

    @Test
    void shouldUseDefaultTtlWhenRequestTtlIsNull() {
        MitigationRequest request = new MitigationRequest();
        request.setSourceIp("91.44.12.8");
        request.setReason("Null TTL test");
        request.setTtlSeconds(null);

        when(mitigationRepository.save(any(Mitigation.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Mitigation result = mitigationService.createMitigation(
                request,
                "admin@threatlens.com"
        );

        assertEquals(300, result.getTtlSeconds());
        assertEquals("ACTIVE", result.getStatus());

        verify(auditLogService, times(1)).log(
                eq("admin@threatlens.com"),
                eq("MANUAL_IP_BLOCK"),
                eq("91.44.12.8"),
                eq("SUCCESS"),
                contains("TTL 300 seconds")
        );
    }

    @Test
    void shouldUseDefaultTtlWhenRequestTtlIsZeroOrNegative() {
        MitigationRequest request = new MitigationRequest();
        request.setSourceIp("91.44.12.8");
        request.setReason("Negative TTL test");
        request.setTtlSeconds(-10);

        when(mitigationRepository.save(any(Mitigation.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Mitigation result = mitigationService.createMitigation(
                request,
                "admin@threatlens.com"
        );

        assertEquals(300, result.getTtlSeconds());
        assertEquals("ACTIVE", result.getStatus());

        verify(auditLogService, times(1)).log(
                eq("admin@threatlens.com"),
                eq("MANUAL_IP_BLOCK"),
                eq("91.44.12.8"),
                eq("SUCCESS"),
                contains("TTL 300 seconds")
        );
    }

    @Test
    void shouldReleaseMitigationWhenMitigationExists() {
        Mitigation mitigation = createMitigationEntity(
                1L,
                "45.88.12.90",
                "ACTIVE",
                300
        );

        when(mitigationRepository.findById(1L)).thenReturn(Optional.of(mitigation));
        when(mitigationRepository.save(any(Mitigation.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        boolean result = mitigationService.releaseMitigation(1L);

        assertTrue(result);
        assertEquals("RELEASED", mitigation.getStatus());

        verify(mitigationRepository, times(1)).findById(1L);
        verify(mitigationRepository, times(1)).save(mitigation);
        verify(auditLogService, times(1)).log(
                eq("system"),
                eq("RELEASE_MITIGATION"),
                eq("45.88.12.90"),
                eq("SUCCESS"),
                eq("Mitigation ID 1 was released.")
        );
    }

    @Test
    void shouldReturnFalseWhenReleasingNonExistingMitigation() {
        when(mitigationRepository.findById(99L)).thenReturn(Optional.empty());

        boolean result = mitigationService.releaseMitigation(99L);

        assertFalse(result);

        verify(mitigationRepository, times(1)).findById(99L);
        verify(mitigationRepository, never()).save(any(Mitigation.class));
        verify(auditLogService, never()).log(
                anyString(),
                anyString(),
                anyString(),
                anyString(),
                anyString()
        );
    }

    @Test
    void shouldReturnTrueWhenSourceIpHasActiveNonExpiredMitigation() {
        when(
                mitigationRepository.existsBySourceIpAndStatusAndExpiresAtAfter(
                        eq("45.88.12.90"),
                        eq("ACTIVE"),
                        any(LocalDateTime.class)
                )
        ).thenReturn(true);

        boolean result = mitigationService.isSourceIpBlocked("45.88.12.90");

        assertTrue(result);

        verify(mitigationRepository, times(1))
                .existsBySourceIpAndStatusAndExpiresAtAfter(
                        eq("45.88.12.90"),
                        eq("ACTIVE"),
                        any(LocalDateTime.class)
                );
    }

    @Test
    void shouldReturnFalseWhenSourceIpHasNoActiveMitigation() {
        when(
                mitigationRepository.existsBySourceIpAndStatusAndExpiresAtAfter(
                        eq("8.8.8.8"),
                        eq("ACTIVE"),
                        any(LocalDateTime.class)
                )
        ).thenReturn(false);

        boolean result = mitigationService.isSourceIpBlocked("8.8.8.8");

        assertFalse(result);

        verify(mitigationRepository, times(1))
                .existsBySourceIpAndStatusAndExpiresAtAfter(
                        eq("8.8.8.8"),
                        eq("ACTIVE"),
                        any(LocalDateTime.class)
                );
    }

    private Mitigation createMitigationEntity(
            Long id,
            String sourceIp,
            String status,
            Integer ttlSeconds
    ) {
        Mitigation mitigation = new Mitigation();

        mitigation.setId(id);
        mitigation.setSourceIp(sourceIp);
        mitigation.setReason("Test reason");
        mitigation.setTtlSeconds(ttlSeconds);
        mitigation.setStatus(status);
        mitigation.setCreatedBy("admin@threatlens.com");
        mitigation.setCreatedAt(LocalDateTime.now());
        mitigation.setExpiresAt(LocalDateTime.now().plusSeconds(ttlSeconds));

        return mitigation;
    }
}