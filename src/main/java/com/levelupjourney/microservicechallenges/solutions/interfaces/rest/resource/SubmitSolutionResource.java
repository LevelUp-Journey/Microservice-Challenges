package com.levelupjourney.microservicechallenges.solutions.interfaces.rest.resource;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * Resource for submitting a solution for evaluation
 * The studentId is extracted from the JWT token
 */
@Schema(
    name = "SubmitSolutionResource",
    description = "Request body for submitting a solution for code evaluation and testing"
)
public record SubmitSolutionResource(
        @NotBlank(message = "Solution code cannot be empty")
        @Schema(
            description = "The source code to be evaluated. Must not be empty.",
            example = "function solve(n) {\n  return n * 2;\n}",
            minLength = 1
        )
        String code
) {
    // studentId will be extracted from JWT token
}