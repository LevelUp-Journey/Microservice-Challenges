package com.levelupjourney.microservicechallenges.challenge.domain.model.valueobjects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.UUID;

@Embeddable
public record TeacherId(@Column(columnDefinition = "uuid") UUID id) {
}
