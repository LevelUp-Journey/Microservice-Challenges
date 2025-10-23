package com.levelupjourney.microservicechallenges.solutions.interfaces.rest.resource;

import jakarta.validation.constraints.NotBlank;

/**
 * Resource for updating a solution's code
 * Only the student's code can be updated
 */
public record UpdateSolutionResource(
        @NotBlank(message = "Code cannot be empty")
        String code
) {
}