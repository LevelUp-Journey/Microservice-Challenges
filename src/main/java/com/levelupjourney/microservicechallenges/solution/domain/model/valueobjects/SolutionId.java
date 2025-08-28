package com.levelupjourney.microservicechallenges.solution.domain.model.valueobjects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.UUID;

@Embeddable
public record SolutionId(@Column(columnDefinition = "uuid") UUID id) {
}
