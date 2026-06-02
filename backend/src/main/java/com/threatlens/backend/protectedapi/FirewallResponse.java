package com.threatlens.backend.protectedapi;

public class FirewallResponse {

    private String decision;
    private String reason;
    private String sourceIp;
    private String eventType;
    private Integer riskScore;

    public FirewallResponse() {
    }

    public FirewallResponse(String decision, String reason, String sourceIp, String eventType, Integer riskScore) {
        this.decision = decision;
        this.reason = reason;
        this.sourceIp = sourceIp;
        this.eventType = eventType;
        this.riskScore = riskScore;
    }

    public String getDecision() {
        return decision;
    }

    public String getReason() {
        return reason;
    }

    public String getSourceIp() {
        return sourceIp;
    }

    public String getEventType() {
        return eventType;
    }

    public Integer getRiskScore() {
        return riskScore;
    }

    public void setDecision(String decision) {
        this.decision = decision;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public void setSourceIp(String sourceIp) {
        this.sourceIp = sourceIp;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public void setRiskScore(Integer riskScore) {
        this.riskScore = riskScore;
    }
}