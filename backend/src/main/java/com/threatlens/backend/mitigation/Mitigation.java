package com.threatlens.backend.mitigation;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "mitigations")
public class Mitigation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sourceIp;

    @Column(length = 2000)
    private String reason;

    private Integer ttlSeconds;

    private String status;

    private String createdBy;

    private LocalDateTime createdAt;

    private LocalDateTime expiresAt;

    public Mitigation() {
    }

    public Long getId() {
        return id;
    }

    public String getSourceIp() {
        return sourceIp;
    }

    public String getReason() {
        return reason;
    }

    public Integer getTtlSeconds() {
        return ttlSeconds;
    }

    public String getStatus() {
        return status;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setSourceIp(String sourceIp) {
        this.sourceIp = sourceIp;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public void setTtlSeconds(Integer ttlSeconds) {
        this.ttlSeconds = ttlSeconds;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }
}