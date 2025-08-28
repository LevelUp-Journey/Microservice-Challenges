package com.levelupjourney.microservicechallenges.shared.domain.model.valueobjects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.UUID;

@Embeddable
public record TestId(UUID id) {
}
