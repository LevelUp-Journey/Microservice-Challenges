package com.levelupjourney.microservicechallenges.challenge.domain.model.valueobjects;

import java.util.UUID;

public record Star(UserId userId, UUID challengeId) {
}
