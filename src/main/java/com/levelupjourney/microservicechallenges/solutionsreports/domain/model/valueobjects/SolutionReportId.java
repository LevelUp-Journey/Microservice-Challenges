package com.levelupjourney.microservicechallenges.solutionsreports.domain.model.valueobjects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.UUID;

@Embeddable
public record SolutionReportId(@Column(name = "id") UUID id) {
}
