package com.levelupjourney.microservicechallenges.solutions.domain.model.valueobjects;

import jakarta.persistence.Embeddable;

import java.util.UUID;

@Embeddable
public record SolutionReportId(UUID value) {
}
