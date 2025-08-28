package com.levelupjourney.microservicechallenges.solution.infrastructure.persistence.jpa.repository;

import com.levelupjourney.microservicechallenges.solution.domain.model.aggregates.Solution;
import com.levelupjourney.microservicechallenges.solution.domain.model.valueobjects.SolutionId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SolutionRepository extends JpaRepository<Solution, SolutionId> {
}
