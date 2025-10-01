package com.levelupjourney.microservicechallenges.solutions.interfaces.grpc;

import com.levelupjourney.microservicechallenges.solutions.interfaces.grpc.*;
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
    private CodeExecutionServiceGrpc.CodeExecutionServiceBlockingStub codeExecutionStub;

    /**
     * Execute solution code with test cases using the existing CodeExecutionService
     */
    public ExecutionResponse executeSolution(String codeVersionId, String studentId, String language, 
                                                  String code, List<TestCaseData> tests) {
        try {
            log.info("🚀 Preparing gRPC request to CodeRunner microservice");
            log.info("📤 Request details:");
            log.info("  - Code Version ID: '{}'", codeVersionId);
            log.info("  - Student ID: '{}'", studentId);
            log.info("  - Language: '{}'", language);
            log.info("  - Code length: {} characters", code.length());
            log.info("  - Number of tests: {}", tests.size());

            // Build ExecutionConfig with default values
            ExecutionConfig config = ExecutionConfig.newBuilder()
                    .setTimeoutSeconds(30)          // Default: 30s
                    .setMemoryLimitMb(512)          // Default: 512MB
                    .setEnableNetwork(false)        // Default: false (sin red)
                    .setDebugMode(false)            // Default: false
                    .build();

            // Build ExecutionRequest using the existing proto structure
            ExecutionRequest request = ExecutionRequest.newBuilder()
                    .setSolutionId(studentId + "-" + codeVersionId)  // Create a unique solution ID
                    .setChallengeId(codeVersionId)                   // Use codeVersionId as challengeId
                    .setStudentId(studentId)
                    .setCode(code)
                    .setLanguage(language)
                    .setConfig(config)
                    .build();

            log.info("🔄 Sending gRPC request to CodeRunner service...");
            
            // Execute gRPC call using the existing CodeExecutionService
            ExecutionResponse response = codeExecutionStub.executeCode(request);
            
            log.info("✅ gRPC ExecutionResponse received:");
            log.info("  - Success: {}", response.getSuccess());
            log.info("  - Approved Tests: {} tests", response.getApprovedTestIdsList().size());
            log.info("  - Execution ID: '{}'", response.getExecutionId());
            log.info("  - Message: '{}'", response.getMessage());
            
            // Extract timing information from metadata
            double timeTaken = 0.0;
            if (response.hasMetadata()) {
                timeTaken = response.getMetadata().getExecutionTimeMs();
                log.info("  - Execution Time: {} ms", timeTaken);
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