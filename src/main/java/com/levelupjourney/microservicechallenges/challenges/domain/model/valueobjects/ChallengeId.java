package com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.util.UUID;

@Embeddable
public record ChallengeId(@Column(name = "id") UUID id) {
}
