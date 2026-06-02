package com.threatlens.backend.alert;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/logs")
public class LogController {

    private final AlertService alertService;

    public LogController(AlertService alertService) {
        this.alertService = alertService;
    }

    @PostMapping("/ingest")
    public Alert ingestLog(@RequestBody LogIngestRequest request) {
        return alertService.ingestLog(request);
    }
}