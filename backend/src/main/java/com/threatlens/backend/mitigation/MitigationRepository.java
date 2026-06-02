package com.threatlens.backend.mitigation;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface MitigationRepository extends JpaRepository<Mitigation, Long> {

    List<Mitigation> findAllByOrderByCreatedAtDesc();

    boolean existsBySourceIpAndStatusAndExpiresAtAfter(
            String sourceIp,
            String status,
            LocalDateTime now
    );
}