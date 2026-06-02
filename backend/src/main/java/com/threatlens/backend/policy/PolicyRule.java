package com.threatlens.backend.policy;

import jakarta.persistence.*;

@Entity
@Table(name = "policy_rules")
public class PolicyRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String eventType;

    @Enumerated(EnumType.STRING)
    private PolicyMode mode;

    private Integer riskScore;

    private String description;

    public PolicyRule() {
    }

    public PolicyRule(String eventType, PolicyMode mode, Integer riskScore, String description) {
        this.eventType = eventType;
        this.mode = mode;
        this.riskScore = riskScore;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public String getEventType() {
        return eventType;
    }

    public PolicyMode getMode() {
        return mode;
    }

    public Integer getRiskScore() {
        return riskScore;
    }

    public String getDescription() {
        return description;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public void setMode(PolicyMode mode) {
        this.mode = mode;
    }

    public void setRiskScore(Integer riskScore) {
        this.riskScore = riskScore;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}