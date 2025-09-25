package com.levelupjourney.microservicechallenges.challenges.domain.model.entities;

import jakarta.persistence.Embeddable;

@Embeddable
public record ChallengeTag(Long id, String name, String color, String iconUrl ) {
}
