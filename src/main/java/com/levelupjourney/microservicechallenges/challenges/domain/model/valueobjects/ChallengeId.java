package com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects;

import jakarta.persistence.Embeddable;

import java.util.UUID;

@Embeddable
public record ChallengeId(UUID id) {
}
