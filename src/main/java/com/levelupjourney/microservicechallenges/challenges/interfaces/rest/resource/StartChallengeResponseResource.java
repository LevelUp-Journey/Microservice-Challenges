package com.levelupjourney.microservicechallenges.challenges.interfaces.rest.resource;

public record StartChallengeResponseResource(
        String challengeId,
        String codeVersionId,
        String studentId
) {
}