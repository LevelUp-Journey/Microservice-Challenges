package com.levelupjourney.microservicechallenges.solutions.interfaces.rest.resource;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Resource representing a Solution with score information.
 * Used for API responses to display solution details including earned points.
 */
@Schema(
    name = "SolutionResource",
    description = "Represents a student's solution to a coding challenge with all metadata and scoring information"
)
public record SolutionResource(
    @Schema(description = "Unique identifier of the solution", example = "550e8400-e29b-41d4-a716-446655440000")
    String id,
    
    @Schema(description = "Unique identifier of the challenge", example = "550e8400-e29b-41d4-a716-446655440001")
    String challengeId,
    
    @Schema(description = "Unique identifier of the code version", example = "550e8400-e29b-41d4-a716-446655440002")
    String codeVersionId,
    
    @Schema(description = "Unique identifier of the student who created the solution", example = "550e8400-e29b-41d4-a716-446655440003")
    String studentId,
    
    @Schema(description = "Number of submission attempts for this solution", example = "3")
    Integer attempts,
    
    @Schema(description = "The student's source code", example = "function solve(n) { return n * 2; }")
    String code,
    
    @Schema(description = "Timestamp of the last submission attempt in ISO 8601 format", example = "2025-10-23T14:30:00Z")
    String lastAttemptAt,
    
    @Schema(
        description = "Current status of the solution",
        example = "IN_PROGRESS",
        allowableValues = {"NO_TESTED", "IN_PROGRESS", "SUCCESS", "FAILED", "MAX_ATTEMPTS_REACHED"}
    )
    String status,
    
    @Schema(description = "Points earned from passed tests", example = "85")
    Integer pointsEarned,
    
    @Schema(description = "Maximum points available for this challenge", example = "100")
    Integer maxPoints,
    
    @Schema(description = "Percentage of tests successfully passed", example = "85.5")
    Double successPercentage
) {
}