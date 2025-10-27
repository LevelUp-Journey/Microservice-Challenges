package com.levelupjourney.microservicechallenges.solutions.interfaces.rest.resource;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/**
 * Resource representing the result of a solution submission and evaluation
 */
@Schema(
    name = "SubmissionResultResource",
    description = "Response containing the evaluation results of a submitted solution including test results and performance metrics"
)
public record SubmissionResultResource(
        @Schema(
            description = "Unique identifier of the solution report generated after evaluation",
            example = "550e8400-e29b-41d4-a716-446655440005",
            nullable = true
        )
        String solutionReportId,
        
        @Schema(
            description = "Human-readable message describing the submission result",
            example = "7 out of 10 tests passed successfully"
        )
        String message,
        
        @Schema(
            description = "Whether all tests passed (true) or some failed (false)",
            example = "false"
        )
        boolean success,
        
        @Schema(
            description = "List of test IDs that passed the evaluation",
            example = "[\"test_1\", \"test_2\", \"test_5\"]"
        )
        List<String> approvedTestIds,
        
        @Schema(
            description = "Total number of tests executed",
            example = "10"
        )
        int totalTests,
        
        @Schema(
            description = "Number of tests that passed",
            example = "7"
        )
        int passedTests,
        
        @Schema(
            description = "Detailed execution information including compiler/runtime errors or warnings",
            example = "Line 5: Warning - unused variable 'x'",
            nullable = true
        )
        String executionDetails,
        
        @Schema(
            description = "Time taken to execute all tests in seconds",
            example = "0.234"
        )
        double timeTaken
) {
}