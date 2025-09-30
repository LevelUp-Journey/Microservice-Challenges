package com.levelupjourney.microservicechallenges.solutions.interfaces.grpc;

import io.grpc.StatusRuntimeException;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

/**
 * gRPC client service for communicating with the CodeRunner microservice.
 * This class is part of the interfaces layer of the solutions bounded context.
 * 100% aligned with CodeRunner implementation according to grpc-implementation.md
 */
@Service
@Slf4j
public class CodeExecutionGrpcClientService {

    @GrpcClient("code-runner")
    private CodeExecutionServiceGrpc.CodeExecutionServiceBlockingStub codeExecutionStub;

    public ExecutionResponse executeCode(String solutionId, String challengeId, String studentId,
                                       String code, String language) {
        try {
            log.info("🚀 Preparing gRPC request to CodeRunner microservice");

            // Build ExecutionConfig with default values per grpc-implementation.md
            ExecutionConfig config = ExecutionConfig.newBuilder()
                    .setTimeoutSeconds(30)          // Default: 30s
                    .setMemoryLimitMb(512)          // Default: 512MB
                    .setEnableNetwork(false)        // Default: false (sin red)
                    .setDebugMode(false)            // Default: false
                    // environment_variables defaults to empty map
                    .build();

            ExecutionRequest request = ExecutionRequest.newBuilder()
                    .setSolutionId(solutionId)
                    .setChallengeId(challengeId)
                    .setStudentId(studentId)
                    .setCode(code)
                    .setLanguage(language)
                    .setConfig(config)              // Include full configuration
                    .build();

            // 📝 LOG DETALLADO DEL REQUEST
            log.info("📤 gRPC ExecutionRequest details:");
            log.info("  - Solution ID: '{}'", request.getSolutionId());
            log.info("  - Challenge ID: '{}'", request.getChallengeId());
            log.info("  - Student ID: '{}'", request.getStudentId());
            log.info("  - Language: '{}'", request.getLanguage());
            log.info("  - Code length: {} characters", request.getCode().length());
            log.info("  - Code preview (first 200 chars): '{}'", 
                    request.getCode().length() > 200 ? 
                    request.getCode().substring(0, 200) + "..." : 
                    request.getCode());
            
            // Log ExecutionConfig details
            if (request.hasConfig()) {
                var reqConfig = request.getConfig();
                log.info("  - ExecutionConfig:");
                log.info("    * Timeout: {} seconds", reqConfig.getTimeoutSeconds());
                log.info("    * Memory Limit: {} MB", reqConfig.getMemoryLimitMb());
                log.info("    * Network Enabled: {}", reqConfig.getEnableNetwork());
                log.info("    * Debug Mode: {}", reqConfig.getDebugMode());
                log.info("    * Environment Variables: {} items", reqConfig.getEnvironmentVariablesMap().size());
                if (!reqConfig.getEnvironmentVariablesMap().isEmpty()) {
                    log.info("    * Environment Variables: {}", reqConfig.getEnvironmentVariablesMap());
                }
            }
            
            // Log the complete protobuf request in text format for debugging
            log.debug("📋 Complete ExecutionRequest (protobuf text format):\n{}", request.toString());
            
            // 🆕 LOG JSON-LIKE REPRESENTATION OF REQUEST (manual formatting)
            try {
                String requestSummary = String.format("""
                        📤 gRPC ExecutionRequest JSON-like representation:
                        {
                          "solution_id": "%s",
                          "challenge_id": "%s", 
                          "student_id": "%s",
                          "language": "%s",
                          "code": "%s",
                          "config": {
                            "timeout_seconds": %d,
                            "memory_limit_mb": %d,
                            "enable_network": %s,
                            "debug_mode": %s,
                            "environment_variables": %s
                          }
                        }""",
                        request.getSolutionId(),
                        request.getChallengeId(),
                        request.getStudentId(),
                        request.getLanguage(),
                        request.getCode().length() > 100 ? 
                            request.getCode().substring(0, 100) + "... (truncated)" : 
                            request.getCode(),
                        request.hasConfig() ? request.getConfig().getTimeoutSeconds() : 30,
                        request.hasConfig() ? request.getConfig().getMemoryLimitMb() : 512,
                        request.hasConfig() ? request.getConfig().getEnableNetwork() : false,
                        request.hasConfig() ? request.getConfig().getDebugMode() : false,
                        request.hasConfig() ? request.getConfig().getEnvironmentVariablesMap().toString() : "{}"
                );
                log.info(requestSummary);
            } catch (Exception e) {
                log.warn("Could not format ExecutionRequest representation: {}", e.getMessage());
            }
            
            // 🔄 EXECUTING GRPC CALL
            log.info("🔄 Sending gRPC request to CodeRunner service...");
            ExecutionResponse response = codeExecutionStub.executeCode(request);
            
            // 📥 LOG DETALLADO DE LA RESPONSE
            log.info("✅ gRPC ExecutionResponse received:");
            log.info("  - Success: {}", response.getSuccess());
            log.info("  - Message: '{}'", response.getMessage());
            log.info("  - Execution ID: '{}'", response.getExecutionId());
            log.info("  - Approved Test IDs: {} tests passed", response.getApprovedTestIdsList().size());
            if (!response.getApprovedTestIdsList().isEmpty()) {
                log.info("  - Approved Test IDs: {}", response.getApprovedTestIdsList());
            }
            
            // Log detailed metadata if available
            if (response.hasMetadata()) {
                var metadata = response.getMetadata();
                log.info("  - Execution Metadata:");
                log.info("    * Execution Time: {} ms", metadata.getExecutionTimeMs());
                log.info("    * Memory Used: {} MB", metadata.getMemoryUsedMb());
                log.info("    * Exit Code: {}", metadata.getExitCode());
                log.info("    * Started At: {}", metadata.getStartedAt());
                log.info("    * Completed At: {}", metadata.getCompletedAt());
                
                if (metadata.hasCompilation()) {
                    var compilation = metadata.getCompilation();
                    log.info("    * Compilation Result:");
                    log.info("      - Success: {}", compilation.getSuccess());
                    log.info("      - Compilation Time: {} ms", compilation.getCompilationTimeMs());
                    if (!compilation.getErrorMessage().isEmpty()) {
                        log.info("      - Error Message: '{}'", compilation.getErrorMessage());
                    }
                    if (!compilation.getWarningsList().isEmpty()) {
                        log.info("      - Warnings: {}", compilation.getWarningsList());
                    }
                }
                
                if (!metadata.getTestResultsList().isEmpty()) {
                    log.info("    * Test Results: {} tests executed", metadata.getTestResultsCount());
                    for (var testResult : metadata.getTestResultsList()) {
                        log.info("      - Test '{}': {} ({}ms)", 
                                testResult.getTestId(), 
                                testResult.getPassed() ? "PASSED" : "FAILED",
                                testResult.getExecutionTimeMs());
                        if (!testResult.getPassed() && !testResult.getErrorMessage().isEmpty()) {
                            log.info("        Error: '{}'", testResult.getErrorMessage());
                        }
                    }
                }
            }
            
            // Log pipeline steps if available
            if (!response.getPipelineStepsList().isEmpty()) {
                log.info("  - Pipeline Steps: {} steps executed", response.getPipelineStepsCount());
                for (var step : response.getPipelineStepsList()) {
                    log.info("    * Step {}: '{}' - Status: {} ({}ms)", 
                            step.getStepOrder(), 
                            step.getName(), 
                            step.getStatus(),
                            step.hasCompletedAt() && step.hasStartedAt() ? 
                                (step.getCompletedAt().getSeconds() - step.getStartedAt().getSeconds()) * 1000 : 0);
                }
            }
            
            // Log the complete protobuf response in text format for debugging
            log.debug("📋 Complete ExecutionResponse (protobuf text format):\n{}", response.toString());
            
            // 🆕 LOG JSON-LIKE REPRESENTATION OF RESPONSE (manual formatting)
            try {
                StringBuilder responseSummary = new StringBuilder();
                responseSummary.append("📥 gRPC ExecutionResponse JSON-like representation:\n");
                responseSummary.append("{\n");
                responseSummary.append(String.format("  \"success\": %s,\n", response.getSuccess()));
                responseSummary.append(String.format("  \"message\": \"%s\",\n", response.getMessage()));
                responseSummary.append(String.format("  \"execution_id\": \"%s\",\n", response.getExecutionId()));
                responseSummary.append(String.format("  \"approved_test_ids\": %s,\n", response.getApprovedTestIdsList()));
                
                if (response.hasMetadata()) {
                    var metadata = response.getMetadata();
                    responseSummary.append("  \"metadata\": {\n");
                    responseSummary.append(String.format("    \"execution_time_ms\": %d,\n", metadata.getExecutionTimeMs()));
                    responseSummary.append(String.format("    \"memory_used_mb\": %d,\n", metadata.getMemoryUsedMb()));
                    responseSummary.append(String.format("    \"exit_code\": %d,\n", metadata.getExitCode()));
                    responseSummary.append(String.format("    \"test_results_count\": %d\n", metadata.getTestResultsCount()));
                    responseSummary.append("  },\n");
                }
                
                responseSummary.append(String.format("  \"pipeline_steps_count\": %d\n", response.getPipelineStepsCount()));
                responseSummary.append("}");
                
                log.info(responseSummary.toString());
            } catch (Exception e) {
                log.warn("Could not format ExecutionResponse representation: {}", e.getMessage());
            }
            
            log.info("✅ gRPC execution completed successfully. Total approved tests: {}", 
                    response.getApprovedTestIdsList().size());
            
            return response;
            
        } catch (StatusRuntimeException e) {
            log.error("gRPC error executing code for solution {}: {}", solutionId, e.getStatus().getDescription());
            throw e;
        }
    }

    /**
     * Executes code with custom execution configuration
     */
    public ExecutionResponse executeCodeWithConfig(String solutionId, String challengeId, String studentId, 
                                                  String code, String language, ExecutionConfig config) {
        try {
            log.info("🚀 Preparing gRPC request with CUSTOM CONFIG to CodeRunner microservice");
            log.info("📊 Request parameters - Solution ID: {}, Challenge ID: {}, Student ID: {}, Language: {}", 
                    solutionId, challengeId, studentId, language);
            
            ExecutionRequest request = ExecutionRequest.newBuilder()
                    .setSolutionId(solutionId)
                    .setChallengeId(challengeId)
                    .setStudentId(studentId)
                    .setCode(code)
                    .setLanguage(language)
                    .setConfig(config)
                    .build();

            // 📝 LOG DETALLADO DEL REQUEST CON CONFIG PERSONALIZADA
            log.info("📤 gRPC ExecutionRequest with CUSTOM CONFIG details:");
            log.info("  - Solution ID: '{}'", request.getSolutionId());
            log.info("  - Challenge ID: '{}'", request.getChallengeId());
            log.info("  - Student ID: '{}'", request.getStudentId());
            log.info("  - Language: '{}'", request.getLanguage());
            log.info("  - Code length: {} characters", request.getCode().length());
            log.info("  - Code preview (first 200 chars): '{}'", 
                    request.getCode().length() > 200 ? 
                    request.getCode().substring(0, 200) + "..." : 
                    request.getCode());
            
            // Log CUSTOM ExecutionConfig details
            if (request.hasConfig()) {
                var reqConfig = request.getConfig();
                log.info("  - CUSTOM ExecutionConfig:");
                log.info("    * Timeout: {} seconds", reqConfig.getTimeoutSeconds());
                log.info("    * Memory Limit: {} MB", reqConfig.getMemoryLimitMb());
                log.info("    * Network Enabled: {}", reqConfig.getEnableNetwork());
                log.info("    * Debug Mode: {}", reqConfig.getDebugMode());
                log.info("    * Environment Variables: {} items", reqConfig.getEnvironmentVariablesMap().size());
                if (!reqConfig.getEnvironmentVariablesMap().isEmpty()) {
                    log.info("    * Environment Variables: {}", reqConfig.getEnvironmentVariablesMap());
                }
            }
            
            log.debug("📋 Complete ExecutionRequest with CUSTOM CONFIG (protobuf text format):\n{}", request.toString());
            
            // 🆕 LOG JSON-LIKE REPRESENTATION OF CUSTOM CONFIG REQUEST (manual formatting)
            try {
                String requestSummary = String.format("""
                        📤 gRPC ExecutionRequest with CUSTOM CONFIG JSON-like representation:
                        {
                          "solution_id": "%s",
                          "challenge_id": "%s",
                          "student_id": "%s",
                          "language": "%s",
                          "code": "%s",
                          "config": {
                            "timeout_seconds": %d,
                            "memory_limit_mb": %d,
                            "enable_network": %s,
                            "debug_mode": %s,
                            "environment_variables": %s
                          }
                        }""",
                        request.getSolutionId(),
                        request.getChallengeId(),
                        request.getStudentId(),
                        request.getLanguage(),
                        request.getCode().length() > 100 ? 
                            request.getCode().substring(0, 100) + "... (truncated)" : 
                            request.getCode(),
                        request.hasConfig() ? request.getConfig().getTimeoutSeconds() : 30,
                        request.hasConfig() ? request.getConfig().getMemoryLimitMb() : 512,
                        request.hasConfig() ? request.getConfig().getEnableNetwork() : false,
                        request.hasConfig() ? request.getConfig().getDebugMode() : false,
                        request.hasConfig() ? request.getConfig().getEnvironmentVariablesMap().toString() : "{}"
                );
                log.info(requestSummary);
            } catch (Exception e) {
                log.warn("Could not format ExecutionRequest with CUSTOM CONFIG representation: {}", e.getMessage());
            }
            
            log.info("🔄 Sending gRPC request with CUSTOM CONFIG to CodeRunner service...");
            ExecutionResponse response = codeExecutionStub.executeCode(request);

            log.info("✅ Custom config execution completed. Success: {}, Execution ID: {}", 
                    response.getSuccess(), response.getExecutionId());
            
            return response;
            
        } catch (StatusRuntimeException e) {
            log.error("gRPC error in custom config execution for solution {}: {}", solutionId, e.getStatus().getDescription());
            throw e;
        }
    }

    /**
     * Gets execution status by execution ID
     */
    public ExecutionResponse getExecutionStatus(String executionId) {
        try {
            log.info("Getting execution status for: {}", executionId);
            
            ExecutionStatusRequest request = ExecutionStatusRequest.newBuilder()
                    .setExecutionId(executionId)
                    .build();

            ExecutionResponse response = codeExecutionStub.getExecutionStatus(request);
            
            log.info("Retrieved execution status for {}: {}", executionId, response.getSuccess());
            
            return response;
            
        } catch (StatusRuntimeException e) {
            log.error("gRPC error getting execution status for {}: {}", executionId, e.getStatus().getDescription());
            throw e;
        }
    }

    /**
     * Performs health check on CodeRunner service
     * 
     * @return true if the service responds with SERVING status, false otherwise
     */
    public boolean isServiceAvailable() {
        try {
            log.debug("Performing health check on CodeRunner service");
            
            HealthCheckRequest request = HealthCheckRequest.newBuilder()
                    .setService("CodeExecutionService")
                    .build();
            
            HealthCheckResponse response = codeExecutionStub.healthCheck(request);
            
            boolean isServing = response.getStatus() == HealthCheckResponse.ServingStatus.SERVING;
            log.debug("CodeRunner service health check - Status: {}, Available: {}", 
                    response.getStatus(), isServing);
            
            return isServing;
            
        } catch (Exception e) {
            log.warn("CodeRunner service health check failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Creates a custom ExecutionConfig with specified parameters
     */
    public static ExecutionConfig createExecutionConfig(int timeoutSeconds, int memoryLimitMb, 
                                                       boolean enableNetwork, boolean debugMode) {
        return ExecutionConfig.newBuilder()
                .setTimeoutSeconds(timeoutSeconds)
                .setMemoryLimitMb(memoryLimitMb)
                .setEnableNetwork(enableNetwork)
                .setDebugMode(debugMode)
                .build();
    }
}