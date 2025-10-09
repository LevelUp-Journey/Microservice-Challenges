package com.levelupjourney.microservicechallenges.solutions.interfaces.rest.resource;

import jakarta.validation.constraints.NotBlank;

public record UpdateSolutionResource(
        @NotBlank(message = "Code cannot be empty")
        String code,
        String language
) {
}