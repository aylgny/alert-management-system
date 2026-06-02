package com.threatlens.backend.protectedapi;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class LoginRequest {

    @NotBlank(message = "Source IP is required")
    @Pattern(
            regexp = "^((25[0-5]|2[0-4][0-9]|1?[0-9]?[0-9])\\.){3}(25[0-5]|2[0-4][0-9]|1?[0-9]?[0-9])$",
            message = "Source IP must be a valid IPv4 address"
    )
    private String sourceIp;

    @NotBlank(message = "Username is required")
    @Size(max = 255, message = "Username must be at most 255 characters")
    private String username;

    @NotBlank(message = "Password is required")
    @Size(max = 255, message = "Password must be at most 255 characters")
    private String password;

    public String getSourceIp() {
        return sourceIp;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setSourceIp(String sourceIp) {
        this.sourceIp = sourceIp;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}