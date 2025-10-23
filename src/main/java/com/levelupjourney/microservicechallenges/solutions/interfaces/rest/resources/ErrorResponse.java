package com.levelupjourney.microservicechallenges.solutions.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Error response resource for returning error messages to clients
 */
@Schema(
    name = "ErrorResponse",
    description = "Standard error response containing a descriptive error message"
)
public record ErrorResponse(
        @Schema(
            description = "Human-readable error message describing what went wrong",
            example = "Solution not found with id: 550e8400-e29b-41d4-a716-446655440000"
        )
        String message
) {
}
