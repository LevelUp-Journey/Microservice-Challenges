package com.levelupjourney.microservicechallenges.solutions.application.internal.outboundservices.grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class CodeExecutionGrpcService {

    @Value("${grpc.client.code-runner.address:dns:///localhost:8084}")
    private String codeRunnerAddress;

    private ManagedChannel channel;

    @PostConstruct
    public void init() {
        // Extract host and port from address
        String address = codeRunnerAddress.replace("dns:///", "");
        String[] parts = address.split(":");
        String host = parts[0];
        int port = parts.length > 1 ? Integer.parseInt(parts[1]) : 8084;

        this.channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();
    }

    @PreDestroy
    public void cleanup() {
        if (channel != null) {
            channel.shutdown();
            try {
                if (!channel.awaitTermination(5, TimeUnit.SECONDS)) {
                    channel.shutdownNow();
                }
            } catch (InterruptedException e) {
                channel.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }

    public ExecutionResult executeCode(String solutionId, String challengeId, String studentId, 
                                     String code, String language) {
        try {
            // TODO: Replace with actual gRPC call using generated stubs
            // This is a placeholder implementation until gRPC classes are generated
            
            // Simulate gRPC call to CodeRunner microservice
            // The actual implementation would use the generated stubs:
            /*
            CodeExecutionServiceGrpc.CodeExecutionServiceBlockingStub stub = 
                CodeExecutionServiceGrpc.newBlockingStub(channel);
            
            ExecutionRequest request = ExecutionRequest.newBuilder()
                .setSolutionId(solutionId)
                .setChallengeId(challengeId)
                .setStudentId(studentId)
                .setCode(code)
                .setLanguage(language)
                .build();
            
            ExecutionResponse response = stub.executeCode(request);
            return new ExecutionResult(response.getSuccess(), 
                                     response.getApprovedTestIdsList(), 
                                     response.getMessage());
            */
            
            // Simulation - replace with real gRPC call
            List<String> approvedTestIds = simulateCodeExecution(code, language);
            return new ExecutionResult(true, approvedTestIds, 
                "Code executed successfully via gRPC (simulated until classes are generated)");
            
        } catch (Exception e) {
            return new ExecutionResult(false, List.of(), 
                "gRPC call failed: " + e.getMessage());
        }
    }

    private List<String> simulateCodeExecution(String code, String language) {
        // Simulate different test results based on code content
        if (code.contains("return") && code.length() > 50) {
            return List.of("test_1", "test_2", "test_3", "test_4"); // Most tests pass
        } else if (code.contains("System.out.println") || code.contains("print")) {
            return List.of("test_1", "test_3"); // Some tests pass
        } else {
            return List.of("test_1"); // Only basic test passes
        }
    }

    // Result wrapper class
    public static class ExecutionResult {
        private final boolean success;
        private final List<String> approvedTestIds;
        private final String message;

        public ExecutionResult(boolean success, List<String> approvedTestIds, String message) {
            this.success = success;
            this.approvedTestIds = approvedTestIds;
            this.message = message;
        }

        public boolean isSuccess() {
            return success;
        }

        public List<String> getApprovedTestIds() {
            return approvedTestIds;
        }

        public String getMessage() {
            return message;
        }
    }
}