package com.threatlens.backend.protectedapi;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/protected")
public class ProtectedApiController {

    private final RequestInspectionService requestInspectionService;

    public ProtectedApiController(RequestInspectionService requestInspectionService) {
        this.requestInspectionService = requestInspectionService;
    }

    @PostMapping("/login")
    public ResponseEntity<FirewallResponse> protectedLogin(@Valid @RequestBody LoginRequest request) {
        FirewallResponse response = requestInspectionService.inspectLoginRequest(request);

        if ("BLOCKED".equals(response.getDecision())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

        return ResponseEntity.ok(response);
    }
}