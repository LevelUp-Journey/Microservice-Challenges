package com.levelupjourney.microservicechallenges.shared.interfaces.rest.resources;

public record CodeVersionTestForSubmittingResource(
        String id,
        String input,
        String expectedOutput,
        String customValidationCode
) {
}