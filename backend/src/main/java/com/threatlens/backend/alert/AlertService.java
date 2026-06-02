package com.threatlens.backend.alert;

import org.springframework.stereotype.Service;
import com.threatlens.backend.audit.AuditLogService;
import java.util.List;

@Service
public class AlertService {

    private final AlertRepository alertRepository;
    private final DetectionService detectionService;
    private final AuditLogService auditLogService;

    public AlertService(
            AlertRepository alertRepository,
            DetectionService detectionService,
            AuditLogService auditLogService
    ) {
        this.alertRepository = alertRepository;
        this.detectionService = detectionService;
        this.auditLogService = auditLogService;
    }

    public List<Alert> getAllAlerts() {
        return alertRepository.findAll();
    }

    public Alert createAlert(Alert alert) {
        String severity = detectionService.detectSeverity(alert.getEventType());
        Integer riskScore = detectionService.calculateRiskScore(severity, alert.getEventType());

        alert.setSeverity(severity);
        alert.setRiskScore(riskScore);

        if (alert.getStatus() == null) {
            alert.setStatus("NEW");
        }

        return alertRepository.save(alert);
    }

    public Alert getAlertById(Long id) {
        return alertRepository.findById(id).orElse(null);
    }

    public Alert updateAlertStatus(Long id, String status) {
        Alert alert = getAlertById(id);

        if (alert == null) {
            return null;
        }

        String oldStatus = alert.getStatus();

        alert.setStatus(status);

        Alert updatedAlert = alertRepository.save(alert);

        auditLogService.log(
                "admin",
                "UPDATE_ALERT_STATUS",
                "alert-" + id,
                "SUCCESS",
                "Alert status changed from " + oldStatus + " to " + status
        );

        return updatedAlert;
    }

    public Alert ingestLog(LogIngestRequest request) {
        Alert alert = new Alert();

        alert.setTitle(generateTitle(request.getEventType()));
        alert.setDescription(generateDescription(request.getEventType()));
        alert.setSourceIp(request.getSourceIp());
        alert.setTargetIp(request.getTargetIp());
        alert.setEventType(request.getEventType());
        alert.setSourceSystem(request.getSourceSystem());
        alert.setRawMessage(request.getRawMessage());
        alert.setStatus("NEW");

        return createAlert(alert);
    }

    private String generateTitle(String eventType) {
        if ("FAILED_LOGIN".equals(eventType)) {
            return "Multiple failed login attempts";
        }

        if ("PORT_SCAN".equals(eventType)) {
            return "Port scan detected";
        }

        if ("HONEYPOT_ACCESS".equals(eventType)) {
            return "Honeypot access detected";
        }

        if ("ADMIN_LOGIN".equals(eventType)) {
            return "Suspicious admin login";
        }

        if ("FORBIDDEN_ACCESS".equals(eventType)) {
            return "Repeated forbidden access detected";
        }

        return "Security event detected";
    }

    private String generateDescription(String eventType) {
        if ("FAILED_LOGIN".equals(eventType)) {
            return "Several failed login attempts were detected from the same source IP.";
        }

        if ("PORT_SCAN".equals(eventType)) {
            return "A source IP attempted to connect to multiple ports in a short time.";
        }

        if ("HONEYPOT_ACCESS".equals(eventType)) {
            return "An external IP attempted to access a honeypot service.";
        }

        if ("ADMIN_LOGIN".equals(eventType)) {
            return "An admin login attempt was detected from an unknown or suspicious IP address.";
        }

        if ("FORBIDDEN_ACCESS".equals(eventType)) {
            return "Multiple forbidden access attempts were detected.";
        }

        return "A security-related event was detected and requires review.";
    }

    public boolean deleteAlert(Long id) {
        if (!alertRepository.existsById(id)) {
            return false;
        }

        alertRepository.deleteById(id);
        return true;
    }
}