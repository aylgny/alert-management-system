package com.threatlens.backend.policy;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public class PolicyUpdateRequest {

    private PolicyMode mode;

    @Min(value = 0, message = "Risk score must be at least 0")
    @Max(value = 100, message = "Risk score must be at most 100")
    private Integer riskScore;

    @Size(max = 1000, message = "Description must be at most 1000 characters")
    private String description;

    public PolicyMode getMode() {
        return mode;
    }

    public Integer getRiskScore() {
        return riskScore;
    }

    public String getDescription() {
        return description;
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