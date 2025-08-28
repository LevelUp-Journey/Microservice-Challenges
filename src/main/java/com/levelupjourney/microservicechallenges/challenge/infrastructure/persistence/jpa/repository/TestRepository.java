package com.levelupjourney.microservicechallenges.challenge.infrastructure.persistence.jpa.repository;

import com.levelupjourney.microservicechallenges.challenge.domain.model.aggregates.Test;
import com.levelupjourney.microservicechallenges.shared.domain.model.valueobjects.TestId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestRepository extends JpaRepository<Test, TestId> {
}
