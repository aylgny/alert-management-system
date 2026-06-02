package com.threatlens.backend.reputation;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reputation")
public class ReputationController {

    private final ReputationService reputationService;

    public ReputationController(ReputationService reputationService) {
        this.reputationService = reputationService;
    }

    @GetMapping
    public List<ReputationEntry> getSourceReputation() {
        return reputationService.getSourceReputation();
    }
}