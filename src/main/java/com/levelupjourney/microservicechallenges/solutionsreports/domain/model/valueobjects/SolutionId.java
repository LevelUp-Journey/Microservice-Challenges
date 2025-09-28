package com.levelupjourney.microservicechallenges.solutionsreports.domain.model.valueobjects;

import jakarta.persistence.Embeddable;
import java.util.UUID;

@Embeddable
public record SolutionId(UUID id) {
}
