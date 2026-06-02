package com.threatlens.backend.reputation;

import java.util.Set;

public class ReputationEntry {

    private String sourceIp;
    private Long eventCount;
    private Integer maxRiskScore;
    private String reputationStatus;
    private Set<String> eventTypes;

    public ReputationEntry() {
    }

    public ReputationEntry(
            String sourceIp,
            Long eventCount,
            Integer maxRiskScore,
            String reputationStatus,
            Set<String> eventTypes
    ) {
        this.sourceIp = sourceIp;
        this.eventCount = eventCount;
        this.maxRiskScore = maxRiskScore;
        this.reputationStatus = reputationStatus;
        this.eventTypes = eventTypes;
    }

    public String getSourceIp() {
        return sourceIp;
    }

    public Long getEventCount() {
        return eventCount;
    }

    public Integer getMaxRiskScore() {
        return maxRiskScore;
    }

    public String getReputationStatus() {
        return reputationStatus;
    }

    public Set<String> getEventTypes() {
        return eventTypes;
    }

    public void setSourceIp(String sourceIp) {
        this.sourceIp = sourceIp;
    }

    public void setEventCount(Long eventCount) {
        this.eventCount = eventCount;
    }

    public void setMaxRiskScore(Integer maxRiskScore) {
        this.maxRiskScore = maxRiskScore;
    }

    public void setReputationStatus(String reputationStatus) {
        this.reputationStatus = reputationStatus;
    }

    public void setEventTypes(Set<String> eventTypes) {
        this.eventTypes = eventTypes;
    }
}