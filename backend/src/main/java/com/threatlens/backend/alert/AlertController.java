package com.threatlens.backend.alert;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.http.ResponseEntity;

import java.util.List;

@RestController
@RequestMapping("/api/alerts")
@CrossOrigin(origins = "http://localhost:5173")
public class AlertController {

    private final AlertService alertService;

    public AlertController(AlertService alertService) {
        this.alertService = alertService;
    }

    @GetMapping
    public List<Alert> getAlerts() {
        return alertService.getAllAlerts();
    }

    @PostMapping
    public Alert createAlert(@RequestBody Alert alert) {
        return alertService.createAlert(alert);
    }

    @GetMapping("/{id}")
    public Alert getAlertById(@PathVariable Long id) {
        return alertService.getAlertById(id);
    }

    @PatchMapping("/{id}/status")
    public Alert updateAlertStatus(@PathVariable Long id, @RequestBody String status) {
        return alertService.updateAlertStatus(id, status);
    }

    @DeleteMapping("/{id}")
    public
    ResponseEntity<Void> deleteAlert(@PathVariable Long id) {
        boolean deleted = alertService.deleteAlert(id);

        if (!deleted) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }
}