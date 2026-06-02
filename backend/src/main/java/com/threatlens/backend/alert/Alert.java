package com.threatlens.backend.alert;

import jakarta.persistence.*;

@Entity
@Table(name = "alerts")
public class Alert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(length = 2000)
    private String description;

    private String sourceIp;
    private String targetIp;
    private String eventType;
    private String severity;
    private String status;
    private Integer riskScore;
    private String sourceSystem;

    @Column(length = 5000)
    private String rawMessage;

    public Alert() {
    }

    public Alert(Long id, String title, String description, String sourceIp, String targetIp,
                 String eventType, String severity, String status, Integer riskScore) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.sourceIp = sourceIp;
        this.targetIp = targetIp;
        this.eventType = eventType;
        this.severity = severity;
        this.status = status;
        this.riskScore = riskScore;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getSourceIp() {
        return sourceIp;
    }

    public String getTargetIp() {
        return targetIp;
    }

    public String getEventType() {
        return eventType;
    }

    public String getSeverity() {
        return severity;
    }

    public String getStatus() {
        return status;
    }

    public Integer getRiskScore() {
        return riskScore;
    }

    public String getSourceSystem() {
        return sourceSystem;
    }

    public String getRawMessage() {
        return rawMessage;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setSourceIp(String sourceIp) {
        this.sourceIp = sourceIp;
    }

    public void setTargetIp(String targetIp) {
        this.targetIp = targetIp;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setRiskScore(Integer riskScore) {
        this.riskScore = riskScore;
    }

    public void setSourceSystem(String sourceSystem) {
        this.sourceSystem = sourceSystem;
    }

    public void setRawMessage(String rawMessage) {
        this.rawMessage = rawMessage;
    }
}