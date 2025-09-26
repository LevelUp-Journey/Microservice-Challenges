package com.levelupjourney.microservicechallenges.challenges.interfaces.rest.resource;

public record CodeVersionTestResource(
    String id,
    String codeVersionId,
    String input,
    String expectedOutput,
    String customValidationCode,
    String failureMessage
) {
}