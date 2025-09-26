package com.levelupjourney.microservicechallenges.challenges.interfaces.rest.resource;

public record AddCodeVersionTestResource(
    String codeVersionId,
    String input,
    String expectedOutput,
    String customValidationCode,
    String failureMessage
) {
}