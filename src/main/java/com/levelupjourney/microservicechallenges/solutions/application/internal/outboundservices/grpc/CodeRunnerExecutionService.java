package com.levelupjourney.microservicechallenges.solutions.application.internal.outboundservices.grpc;

import com.levelupjourney.microservicechallenges.shared.interfaces.rest.resources.CodeVersionTestForSubmittingResource;
import com.levelupjourney.microservicechallenges.solutions.interfaces.grpc.CodeRunnerGrpcClientService;
import com.levelupjourney.microservicechallenges.solutions.interfaces.grpc.ExecutionResponse;
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
    public CodeExecutionResult executeSolution(String codeVersionId, String studentId, String language,
                                              String code, List<CodeVersionTestForSubmittingResource> tests) {
        try {
            log.info("🎯 Starting code execution process using CodeRunner microservice");
            log.info("📋 Input validation:");
            log.info("  - Code Version ID: '{}'", codeVersionId);
            log.info("  - Student ID: '{}'", studentId);
            log.info("  - Language: '{}'", language);
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
            var response = codeRunnerGrpcClient.executeSolution(codeVersionId, studentId, language, code, testCases);

            // Extract execution time from metadata
            double timeTaken = 0.0;
            if (response.hasMetadata()) {
                timeTaken = response.getMetadata().getExecutionTimeMs();
            }

            // Check if all tests passed
            boolean allTestsPassed = response.getApprovedTestIdsList().size() == tests.size();

            log.info("🎉 Code execution completed:");
            log.info("  - Total tests: {}", tests.size());
            log.info("  - Passed tests: {}", response.getApprovedTestIdsList().size());
            log.info("  - Success rate: {:.1f}%", (response.getApprovedTestIdsList().size() * 100.0) / tests.size());
            log.info("  - All tests passed: {}", allTestsPassed);
            log.info("  - Execution time: {} ms", timeTaken);

            return new CodeExecutionResult(
                    response.getApprovedTestIdsList(),
                    timeTaken,
                    allTestsPassed
            );

        } catch (Exception e) {
            log.error("❌ Failed to execute solution via CodeRunner: {}", e.getMessage(), e);
            throw new RuntimeException("Code execution failed: " + e.getMessage(), e);
        }
    }

    /**
     * Result of code execution
     */
    public record CodeExecutionResult(
            List<String> passedTestsId,
            double timeTaken,
            boolean successful
    ) {}
}