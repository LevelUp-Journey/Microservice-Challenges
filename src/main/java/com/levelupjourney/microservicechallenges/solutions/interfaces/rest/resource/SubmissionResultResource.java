package com.levelupjourney.microservicechallenges.solutions.interfaces.rest.resource;

import java.util.List;

public record SubmissionResultResource(
        String solutionReportId,
        String message,
        boolean success,
        List<String> approvedTestIds,
        int totalTests,
        int passedTests,
        String executionDetails
) {
}