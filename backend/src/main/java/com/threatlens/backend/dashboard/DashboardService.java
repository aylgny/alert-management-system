package com.threatlens.backend.dashboard;

import com.threatlens.backend.alert.Alert;
import com.threatlens.backend.alert.AlertService;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class DashboardService {

    private final AlertService alertService;

    public DashboardService(AlertService alertService) {
        this.alertService = alertService;
    }

    public DashboardSummary getSummary() {
        List<Alert> alerts = alertService.getAllAlerts();

        int totalAlerts = alerts.size();
        int criticalAlerts = 0;
        int highAlerts = 0;
        int newAlerts = 0;
        int totalRiskScore = 0;

        for (Alert alert : alerts) {
            if ("CRITICAL".equals(alert.getSeverity())) {
                criticalAlerts++;
            }

            if ("HIGH".equals(alert.getSeverity())) {
                highAlerts++;
            }

            if ("NEW".equals(alert.getStatus())) {
                newAlerts++;
            }

            if (alert.getRiskScore() != null) {
                totalRiskScore = totalRiskScore + alert.getRiskScore();
            }
        }

        double averageRiskScore = 0;

        if (totalAlerts > 0) {
            averageRiskScore = (double) totalRiskScore / totalAlerts;
        }

        return new DashboardSummary(
                totalAlerts,
                criticalAlerts,
                highAlerts,
                newAlerts,
                averageRiskScore
        );
    }

    public List<DistributionItem> getSeverityDistribution() {
        List<Alert> alerts = alertService.getAllAlerts();

        int lowCount = 0;
        int mediumCount = 0;
        int highCount = 0;
        int criticalCount = 0;

        for (Alert alert : alerts) {
            if ("LOW".equals(alert.getSeverity())) {
                lowCount++;
            }

            if ("MEDIUM".equals(alert.getSeverity())) {
                mediumCount++;
            }

            if ("HIGH".equals(alert.getSeverity())) {
                highCount++;
            }

            if ("CRITICAL".equals(alert.getSeverity())) {
                criticalCount++;
            }
        }

        List<DistributionItem> distribution = new ArrayList<>();

        distribution.add(new DistributionItem("LOW", lowCount));
        distribution.add(new DistributionItem("MEDIUM", mediumCount));
        distribution.add(new DistributionItem("HIGH", highCount));
        distribution.add(new DistributionItem("CRITICAL", criticalCount));

        return distribution;
    }

    public List<DistributionItem> getStatusDistribution() {
        List<Alert> alerts = alertService.getAllAlerts();

        int newCount = 0;
        int investigatingCount = 0;
        int resolvedCount = 0;
        int falsePositiveCount = 0;

        for (Alert alert : alerts) {
            if ("NEW".equals(alert.getStatus())) {
                newCount++;
            }

            if ("INVESTIGATING".equals(alert.getStatus())) {
                investigatingCount++;
            }

            if ("RESOLVED".equals(alert.getStatus())) {
                resolvedCount++;
            }

            if ("FALSE_POSITIVE".equals(alert.getStatus())) {
                falsePositiveCount++;
            }
        }

        List<DistributionItem> distribution = new ArrayList<>();

        distribution.add(new DistributionItem("NEW", newCount));
        distribution.add(new DistributionItem("INVESTIGATING", investigatingCount));
        distribution.add(new DistributionItem("RESOLVED", resolvedCount));
        distribution.add(new DistributionItem("FALSE_POSITIVE", falsePositiveCount));

        return distribution;
    }
}