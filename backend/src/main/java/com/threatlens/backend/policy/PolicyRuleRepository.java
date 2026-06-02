package com.threatlens.backend.policy;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PolicyRuleRepository extends JpaRepository<PolicyRule, Long> {

    Optional<PolicyRule> findByEventType(String eventType);
}