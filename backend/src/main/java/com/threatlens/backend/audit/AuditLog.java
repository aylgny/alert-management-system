package com.threatlens.backend.audit;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime timestamp;

    private String actor;

    private String action;

    private String target;

    private String result;

    @Column(length = 2000)
    private String details;

    public AuditLog() {
    }

    public AuditLog(String actor, String action, String target, String result, String details) {
        this.timestamp = LocalDateTime.now();
        this.actor = actor;
        this.action = action;
        this.target = target;
        this.result = result;
        this.details = details;
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getActor() {
        return actor;
    }

    public String getAction() {
        return action;
    }

    public String getTarget() {
        return target;
    }

    public String getResult() {
        return result;
    }

    public String getDetails() {
        return details;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public void setActor(String actor) {
        this.actor = actor;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}