package com.threatlens.backend.dashboard;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/api/dashboard/summary")
    public DashboardSummary getSummary() {
        return dashboardService.getSummary();
    }

    @GetMapping("/api/dashboard/severity-distribution")
    public List<DistributionItem> getSeverityDistribution() {
        return dashboardService.getSeverityDistribution();
    }

    @GetMapping("/api/dashboard/status-distribution")
    public List<DistributionItem> getStatusDistribution() {
        return dashboardService.getStatusDistribution();
    }
}