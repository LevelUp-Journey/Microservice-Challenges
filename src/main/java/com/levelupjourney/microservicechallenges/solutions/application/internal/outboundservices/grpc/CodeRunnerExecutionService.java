package com.levelupjourney.microservicechallenges.solutions.application.internal.outboundservices.grpc;

import com.levelupjourney.microservicechallenges.shared.interfaces.rest.resources.CodeVersionTestForSubmittingResource;
import com.levelupjourney.microservicechallenges.solutions.interfaces.grpc.CodeRunnerGrpcClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Application service for executing code solutions using the CodeRunner microservice.
 * This service handles the orchestration and transformation of data for code execution.
 */
@Service
@Slf4j
public class CodeRunnerExecutionService {

    private final CodeRunnerGrpcClientService codeRunnerGrpcClient;

    @Autowired
    public CodeRunnerExecutionService(CodeRunnerGrpcClientService codeRunnerGrpcClient) {
        this.codeRunnerGrpcClient = codeRunnerGrpcClient;
    }

    /**
     * Execute solution code with tests using the CodeRunner microservice
     */
    public CodeExecutionResult executeSolution(String challengeId, String codeVersionId, String studentId,
                                              String code, List<CodeVersionTestForSubmittingResource> tests) {
        try {
            log.info("🎯 Starting code execution process using CodeRunner microservice");
            log.info("📋 Input validation:");
            log.info("  - Challenge ID: '{}'", challengeId);
            log.info("  - Code Version ID: '{}'", codeVersionId);
            log.info("  - Student ID: '{}'", studentId);
            log.info("  - Code length: {} characters", code != null ? code.length() : 0);
            log.info("  - Total tests: {}", tests != null ? tests.size() : 0);

            // Transform tests to the format expected by CodeRunner
            var testCases = tests.stream()
                    .map(test -> new CodeRunnerGrpcClientService.TestCaseData(
                            test.id(),
                            test.input(),
                            test.expectedOutput(),
                            test.customValidationCode()
                    ))
                    .toList();

            // Call CodeRunner via gRPC
            var response = codeRunnerGrpcClient.evaluateSolution(challengeId, codeVersionId, studentId, code, testCases);

            log.info("🎉 Code execution completed:");
            log.info("  - Completed: {}", response.getCompleted());
            log.info("  - Success: {}", response.getSuccess());
            log.info("  - Total tests: {}", response.getTotalTests());
            log.info("  - Passed tests: {}", response.getPassedTests());
            log.info("  - Failed tests: {}", response.getFailedTests());
            log.info("  - Success rate: {:.1f}%", 
                    response.getTotalTests() > 0 ? (response.getPassedTests() * 100.0) / response.getTotalTests() : 0);
            log.info("  - Execution time: {} ms", response.getExecutionTimeMs());
            log.info("  - Message: {}", response.getMessage());
            
            if (!response.getSuccess()) {
                log.warn("⚠️ Execution had errors:");
                log.warn("  - Error Type: {}", response.getErrorType());
                log.warn("  - Error Message: {}", response.getErrorMessage());
            }

            return new CodeExecutionResult(
                    response.getApprovedTestsList(),
                    response.getExecutionTimeMs(),
                    response.getSuccess(),
                    response.getTotalTests(),
                    response.getPassedTests(),
                    response.getFailedTests(),
                    response.getMessage(),
                    response.getErrorMessage(),
                    response.getErrorType()
            );

        } catch (Exception e) {
            log.error("❌ Failed to execute solution via CodeRunner: {}", e.getMessage(), e);
            throw new RuntimeException("Code execution failed: " + e.getMessage(), e);
        }
    }

    /**
     * Result of code execution with comprehensive details
     */
    public record CodeExecutionResult(
            List<String> passedTestsId,    // IDs of tests that passed
            long timeTaken,                 // Execution time in milliseconds
            boolean successful,             // True if all tests passed
            int totalTests,                 // Total number of tests
            int passedTests,                // Number of tests that passed
            int failedTests,                // Number of tests that failed
            String message,                 // Descriptive message
            String errorMessage,            // Error message if any
            String errorType                // Error type (timeout, compilation_error, etc.)
    ) {
        /**
         * Check if there are any errors
         */
        public boolean hasErrors() {
            return errorMessage != null && !errorMessage.isEmpty();
        }
        
        /**
         * Check if execution had partial success (some tests passed, some failed)
         */
        public boolean isPartialSuccess() {
            return passedTests > 0 && passedTests < totalTests;
        }
        
        /**
         * Calculate success rate as percentage
         */
        public double getSuccessRate() {
            return totalTests > 0 ? (double) passedTests / totalTests * 100 : 0;
        }
    }
}