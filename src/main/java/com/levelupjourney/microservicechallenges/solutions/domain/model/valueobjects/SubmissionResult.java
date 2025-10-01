package com.levelupjourney.microservicechallenges.solutions.domain.model.valueobjects;

import java.util.List;

/**
 * Value object representing the result of a solution submission
 * Enhanced with detailed execution information from CodeRunner gRPC service
 */
public record SubmissionResult(
        SolutionReportId solutionReportId,
        boolean success,
        List<String> approvedTestIds,
        int totalTests,
        String message,
        String executionDetails,
        double timeTaken
) {
    public int getPassedTests() {
        return approvedTestIds != null ? approvedTestIds.size() : 0;
    }
    
    public static SubmissionResult success(SolutionReportId reportId, List<String> approvedTestIds, 
                                         int totalTests, String message) {
        return new SubmissionResult(reportId, true, approvedTestIds, totalTests, message, 
                                  "Executed via gRPC CodeRunner service", 0.0);
    }
    
    public static SubmissionResult success(SolutionReportId reportId, List<String> approvedTestIds, 
                                         int totalTests, String message, String executionDetails) {
        return new SubmissionResult(reportId, true, approvedTestIds, totalTests, message, executionDetails, 0.0);
    }
    
    public static SubmissionResult success(SolutionReportId reportId, List<String> approvedTestIds, 
                                         int totalTests, String message, String executionDetails, double timeTaken) {
        return new SubmissionResult(reportId, true, approvedTestIds, totalTests, message, executionDetails, timeTaken);
    }
    
    public static SubmissionResult failure(String message) {
        return new SubmissionResult(null, false, List.of(), 0, message, 
                                  "Execution failed", 0.0);
    }
}