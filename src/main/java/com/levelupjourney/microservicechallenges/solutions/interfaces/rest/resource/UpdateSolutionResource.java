package com.levelupjourney.microservicechallenges.solutions.interfaces.rest.resource;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * Resource for updating a solution's code
 * Only the student's code can be updated
 */
@Schema(
    name = "UpdateSolutionResource",
    description = "Request body for updating a solution's code. Only the code field can be modified."
)
public record UpdateSolutionResource(
        @NotBlank(message = "Code cannot be empty")
        @Schema(
            description = "The updated source code for the solution",
            example = "function solve(n) {\n  // Updated implementation\n  return n * 3;\n}",
            minLength = 1
        )
        String code
) {
}