package com.levelupjourney.microservicechallenges.challenges.domain.model.entities;

import jakarta.persistence.Embeddable;

import java.util.UUID;

@Embeddable
public record ChallengeTag(Long id, String name, String color, String iconUrl ) {
}
