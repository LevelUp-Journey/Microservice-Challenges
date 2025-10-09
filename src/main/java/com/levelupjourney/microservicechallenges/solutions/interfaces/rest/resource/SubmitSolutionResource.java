package com.levelupjourney.microservicechallenges.solutions.interfaces.rest.resource;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SubmitSolutionResource(
        @NotBlank(message = "Solution code cannot be empty")
        String code,
        
        @NotNull(message = "Student ID is required")
        String studentId
) {
}