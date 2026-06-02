package com.threatlens.backend.mitigation;

import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mitigations")
public class MitigationController {

    private final MitigationService mitigationService;

    public MitigationController(MitigationService mitigationService) {
        this.mitigationService = mitigationService;
    }

    @GetMapping
    public List<Mitigation> getAllMitigations() {
        return mitigationService.getAllMitigations();
    }

    @PostMapping
    public Mitigation createMitigation(
            @Valid @RequestBody MitigationRequest request,
            Authentication authentication
    ) {
        String createdBy = authentication.getName();

        return mitigationService.createMitigation(request, createdBy);
    }

    @DeleteMapping("/{id}")
    public void releaseMitigation(@PathVariable Long id) {
        mitigationService.releaseMitigation(id);
    }
}