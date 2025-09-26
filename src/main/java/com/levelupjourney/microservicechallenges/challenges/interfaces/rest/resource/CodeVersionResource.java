package com.levelupjourney.microservicechallenges.challenges.interfaces.rest.resource;

public record CodeVersionResource(
    String id,
    String challengeId,
    String language,
    String initialCode
) {
}