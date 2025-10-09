package com.levelupjourney.microservicechallenges.solutions.domain.model.valueobjects;

import java.util.List;

/**
 * Value object representing the result of code execution via gRPC
 */
public record ExecutionResult(
        boolean success,
        List<String> approvedTestIds,
        int totalTests,
        String message
) {
    public int getPassedTests() {
        return approvedTestIds != null ? approvedTestIds.size() : 0;
    }
    
    public double getSuccessRate() {
        if (totalTests == 0) return 0.0;
        return (double) getPassedTests() / totalTests;
    }
}