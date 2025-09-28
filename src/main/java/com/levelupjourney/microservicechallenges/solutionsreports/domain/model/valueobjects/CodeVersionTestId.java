package com.levelupjourney.microservicechallenges.solutionsreports.domain.model.valueobjects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.UUID;

@Embeddable
public record CodeVersionTestId(@Column(name = "id") UUID id) {
}
