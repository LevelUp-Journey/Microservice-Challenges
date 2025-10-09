package com.levelupjourney.microservicechallenges.solutions.interfaces.grpc;

import com.levelupjourney.microservicechallenges.coderunner.grpc.*;
import io.grpc.StatusRuntimeException;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * gRPC client service for communicating with the CodeRunner microservice.
 * This service sends code execution requests and receives results.
 */
@Service
@Slf4j
public class CodeRunnerGrpcClientService {

    @GrpcClient("code-runner")
    private SolutionEvaluationServiceGrpc.SolutionEvaluationServiceBlockingStub evaluationStub;

    /**
     * Evaluate solution code with test cases
     */
    public EvaluateSolutionResponse evaluateSolution(String challengeId, String codeVersionId, String studentId, 
                                                      String code, List<TestCaseData> tests) {
        try {
            log.info("🚀 Preparing gRPC request to CodeRunner microservice");
            log.info("📤 Request details:");
            log.info("  - Challenge ID: '{}'", challengeId);
            log.info("  - Code Version ID: '{}'", codeVersionId);
            log.info("  - Student ID: '{}'", studentId);
            log.info("  - Code length: {} characters", code.length());
            log.info("  - Number of tests: {}", tests.size());

            // Build test cases for the request
            var testCases = tests.stream()
                    .map(test -> TestCase.newBuilder()
                            .setCodeVersionTestId(test.codeVersionTestId())
                            .setInput(test.input())
                            .setExpectedOutput(test.expectedOutput())
                            .setCustomValidationCode(test.customValidationCode() != null ? test.customValidationCode() : "")
                            .build())
                    .toList();

            // Build EvaluateSolutionRequest
            EvaluateSolutionRequest request = EvaluateSolutionRequest.newBuilder()
                    .setChallengeId(challengeId)
                    .setCodeVersionId(codeVersionId)
                    .setStudentId(studentId)
                    .setCode(code)
                    .addAllTests(testCases)
                    .build();

            log.info("🔄 Sending gRPC request to CodeRunner service...");
            
            // Execute gRPC call
            EvaluateSolutionResponse response = evaluationStub.evaluateSolution(request);
            
            log.info("✅ gRPC EvaluateSolutionResponse received:");
            log.info("  - Completed: {}", response.getCompleted());
            log.info("  - Success: {}", response.getSuccess());
            log.info("  - Total Tests: {}", response.getTotalTests());
            log.info("  - Passed Tests: {}", response.getPassedTests());
            log.info("  - Failed Tests: {}", response.getFailedTests());
            log.info("  - Approved Test IDs: {}", response.getApprovedTestsList());
            log.info("  - Execution Time: {} ms", response.getExecutionTimeMs());
            log.info("  - Message: '{}'", response.getMessage());
            
            if (!response.getSuccess()) {
                log.warn("⚠️ Execution had errors:");
                log.warn("  - Error Type: '{}'", response.getErrorType());
                log.warn("  - Error Message: '{}'", response.getErrorMessage());
            }

            return response;
            
        } catch (StatusRuntimeException e) {
            log.error("❌ gRPC call to CodeRunner failed: {} - {}", e.getStatus().getCode(), e.getStatus().getDescription());
            throw new RuntimeException("Failed to execute solution via gRPC: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("❌ Unexpected error during gRPC call to CodeRunner: {}", e.getMessage(), e);
            throw new RuntimeException("Unexpected error during code execution: " + e.getMessage(), e);
        }
    }

    /**
     * Data transfer object for test case information
     */
    public record TestCaseData(
            String codeVersionTestId,
            String input,
            String expectedOutput,
            String customValidationCode
    ) {}
}