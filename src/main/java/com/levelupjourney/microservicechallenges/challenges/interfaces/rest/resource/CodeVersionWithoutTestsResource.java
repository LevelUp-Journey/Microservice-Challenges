package com.levelupjourney.microservicechallenges.challenges.interfaces.rest.resource;

public record CodeVersionWithoutTestsResource(
    String id,
    String challengeId,
    String language,
    String initialCode,
    String functionName
) {
}
