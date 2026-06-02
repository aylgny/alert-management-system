package com.threatlens.backend.dashboard;

public class DashboardSummary {

    private int totalAlerts;
    private int criticalAlerts;
    private int highAlerts;
    private int newAlerts;
    private double averageRiskScore;

    public DashboardSummary() {
    }

    public DashboardSummary(int totalAlerts, int criticalAlerts, int highAlerts,
                            int newAlerts, double averageRiskScore) {
        this.totalAlerts = totalAlerts;
        this.criticalAlerts = criticalAlerts;
        this.highAlerts = highAlerts;
        this.newAlerts = newAlerts;
        this.averageRiskScore = averageRiskScore;
    }

    public int getTotalAlerts() {
        return totalAlerts;
    }

    public void setTotalAlerts(int totalAlerts) {
        this.totalAlerts = totalAlerts;
    }

    public int getCriticalAlerts() {
        return criticalAlerts;
    }

    public void setCriticalAlerts(int criticalAlerts) {
        this.criticalAlerts = criticalAlerts;
    }

    public int getHighAlerts() {
        return highAlerts;
    }

    public void setHighAlerts(int highAlerts) {
        this.highAlerts = highAlerts;
    }

    public int getNewAlerts() {
        return newAlerts;
    }

    public void setNewAlerts(int newAlerts) {
        this.newAlerts = newAlerts;
    }

    public double getAverageRiskScore() {
        return averageRiskScore;
    }

    public void setAverageRiskScore(double averageRiskScore) {
        this.averageRiskScore = averageRiskScore;
    }
}