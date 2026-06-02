package com.threatlens.backend.mitigation;

import jakarta.validation.constraints.*;

public class MitigationRequest {

    @NotBlank(message = "Source IP is required")
    @Pattern(
            regexp = "^((25[0-5]|2[0-4][0-9]|1?[0-9]?[0-9])\\.){3}(25[0-5]|2[0-4][0-9]|1?[0-9]?[0-9])$",
            message = "Source IP must be a valid IPv4 address"
    )
    private String sourceIp;

    @NotBlank(message = "Reason is required")
    @Size(max = 1000, message = "Reason must be at most 1000 characters")
    private String reason;

    @NotNull(message = "TTL seconds is required")
    @Min(value = 1, message = "TTL seconds must be at least 1")
    @Max(value = 86400, message = "TTL seconds must be at most 86400")
    private Integer ttlSeconds;

    public String getSourceIp() {
        return sourceIp;
    }

    public String getReason() {
        return reason;
    }

    public Integer getTtlSeconds() {
        return ttlSeconds;
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
}