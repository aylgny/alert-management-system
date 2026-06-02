package com.threatlens.backend.audit;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public List<AuditLog> getAllAuditLogs() {
        return auditLogRepository.findAllByOrderByTimestampDesc();
    }

    public AuditLog log(String actor, String action, String target, String result, String details) {
        AuditLog auditLog = new AuditLog(actor, action, target, result, details);

        return auditLogRepository.save(auditLog);
    }
}