package com.threatlens.backend.alert;

import org.springframework.stereotype.Service;

@Service
public class DetectionService {

    public String detectSeverity(String eventType) {

        if ("PORT_SCAN".equals(eventType)) {
            return "CRITICAL";
        }

        if ("FAILED_LOGIN".equals(eventType)) {
            return "HIGH";
        }

        if ("HONEYPOT_ACCESS".equals(eventType)) {
            return "HIGH";
        }

        if ("ADMIN_LOGIN".equals(eventType)) {
            return "MEDIUM";
        }

        if ("FORBIDDEN_ACCESS".equals(eventType)) {
            return "MEDIUM";
        }

        return "LOW";
    }

    public Integer calculateRiskScore(String severity, String eventType) {
        int score;

        if ("CRITICAL".equals(severity)) {
            score = 90;
        } else if ("HIGH".equals(severity)) {
            score = 75;
        } else if ("MEDIUM".equals(severity)) {
            score = 50;
        } else {
            score = 25;
        }

        if ("HONEYPOT_ACCESS".equals(eventType)) {
            score = score + 10;
        }

        if ("PORT_SCAN".equals(eventType)) {
            score = score + 5;
        }

        if (score > 100) {
            score = 100;
        }

        return score;
    }
}