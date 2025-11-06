package com.levelupjourney.microservicechallenges.challenges.interfaces.rest.resource;

public record StartChallengeResource(
    String challengeId, 
    String codeVersionId
) {
    // studentId will be extracted from JWT token
}
