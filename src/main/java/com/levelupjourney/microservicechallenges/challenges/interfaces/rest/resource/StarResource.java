package com.levelupjourney.microservicechallenges.challenges.interfaces.rest.resource;

import java.time.LocalDateTime;

public record StarResource(String userId, LocalDateTime starredAt) {
}