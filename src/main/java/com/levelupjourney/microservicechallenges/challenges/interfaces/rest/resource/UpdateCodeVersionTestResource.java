package com.levelupjourney.microservicechallenges.challenges.interfaces.rest.resource;

import java.util.Optional;

public record UpdateCodeVersionTestResource(
    Optional<String> input,
    Optional<String> expectedOutput,
    Optional<String> customValidationCode,
    Optional<String> failureMessage
) {
}