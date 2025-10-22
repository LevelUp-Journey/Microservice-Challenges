package com.levelupjourney.microservicechallenges.solutions.interfaces.rest.resource;

import jakarta.validation.constraints.NotBlank;

public record SubmitSolutionResource(
        @NotBlank(message = "Solution code cannot be empty")
        String code
) {
    // studentId will be extracted from JWT token
}