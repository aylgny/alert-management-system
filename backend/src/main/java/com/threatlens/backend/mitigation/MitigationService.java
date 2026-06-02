package com.threatlens.backend.mitigation;

import com.threatlens.backend.audit.AuditLogService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MitigationService {

    private final MitigationRepository mitigationRepository;
    private final AuditLogService auditLogService;

    public MitigationService(
            MitigationRepository mitigationRepository,
            AuditLogService auditLogService
    ) {
        this.mitigationRepository = mitigationRepository;
        this.auditLogService = auditLogService;
    }

    public List<Mitigation> getAllMitigations() {
        return mitigationRepository.findAllByOrderByCreatedAtDesc();
    }

    public Mitigation createMitigation(MitigationRequest request, String createdBy) {
        Mitigation mitigation = new Mitigation();

        mitigation.setSourceIp(request.getSourceIp());
        mitigation.setReason(request.getReason());

        Integer ttlSeconds = request.getTtlSeconds();

        if (ttlSeconds == null || ttlSeconds <= 0) {
            ttlSeconds = 300;
        }

        mitigation.setTtlSeconds(ttlSeconds);
        mitigation.setStatus("ACTIVE");
        mitigation.setCreatedBy(createdBy);
        mitigation.setCreatedAt(LocalDateTime.now());
        mitigation.setExpiresAt(LocalDateTime.now().plusSeconds(ttlSeconds));

        Mitigation savedMitigation = mitigationRepository.save(mitigation);

        auditLogService.log(
                createdBy,
                "MANUAL_IP_BLOCK",
                request.getSourceIp(),
                "SUCCESS",
                "Manual IP block created with TTL " + ttlSeconds + " seconds. Reason: " + request.getReason()
        );

        return savedMitigation;
    }

    public boolean releaseMitigation(Long id) {
        Mitigation mitigation = mitigationRepository.findById(id).orElse(null);

        if (mitigation == null) {
            return false;
        }

        mitigation.setStatus("RELEASED");
        mitigationRepository.save(mitigation);

        auditLogService.log(
                "system",
                "RELEASE_MITIGATION",
                mitigation.getSourceIp(),
                "SUCCESS",
                "Mitigation ID " + mitigation.getId() + " was released."
        );

        return true;
    }

    public boolean isSourceIpBlocked(String sourceIp) {
        return mitigationRepository.existsBySourceIpAndStatusAndExpiresAtAfter(
                sourceIp,
                "ACTIVE",
                LocalDateTime.now()
        );
    }
}