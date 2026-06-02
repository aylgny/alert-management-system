package com.threatlens.backend.reputation;

import com.threatlens.backend.alert.Alert;
import com.threatlens.backend.alert.AlertRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReputationService {

    private final AlertRepository alertRepository;

    public ReputationService(AlertRepository alertRepository) {
        this.alertRepository = alertRepository;
    }

    public List<ReputationEntry> getSourceReputation() {
        List<Alert> alerts = alertRepository.findAll();

        Map<String, List<Alert>> alertsBySourceIp = alerts.stream()
                .filter(alert -> alert.getSourceIp() != null && !alert.getSourceIp().isBlank())
                .collect(Collectors.groupingBy(Alert::getSourceIp));

        List<ReputationEntry> reputationEntries = new ArrayList<>();

        for (Map.Entry<String, List<Alert>> entry : alertsBySourceIp.entrySet()) {
            String sourceIp = entry.getKey();
            List<Alert> ipAlerts = entry.getValue();

            int maxRiskScore = ipAlerts.stream()
                    .map(Alert::getRiskScore)
                    .filter(Objects::nonNull)
                    .max(Integer::compareTo)
                    .orElse(0);

            Set<String> eventTypes = ipAlerts.stream()
                    .map(Alert::getEventType)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toCollection(LinkedHashSet::new));

            String reputationStatus = calculateReputationStatus(maxRiskScore);

            ReputationEntry reputationEntry = new ReputationEntry(
                    sourceIp,
                    (long) ipAlerts.size(),
                    maxRiskScore,
                    reputationStatus,
                    eventTypes
            );

            reputationEntries.add(reputationEntry);
        }

        reputationEntries.sort(
                Comparator.comparing(ReputationEntry::getMaxRiskScore).reversed()
        );

        return reputationEntries;
    }

    private String calculateReputationStatus(Integer maxRiskScore) {
        if (maxRiskScore == null) {
            return "MONITORED";
        }

        if (maxRiskScore >= 90) {
            return "MALICIOUS";
        }

        if (maxRiskScore >= 70) {
            return "SUSPICIOUS";
        }

        return "MONITORED";
    }
}