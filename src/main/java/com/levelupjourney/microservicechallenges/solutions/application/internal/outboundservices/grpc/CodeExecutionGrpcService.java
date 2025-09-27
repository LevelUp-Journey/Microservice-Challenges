package com.levelupjourney.microservicechallenges.solutions.application.internal.outboundservices.grpc;

import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CodeExecutionGrpcService {

    public ExecutionResult executeCode(String solutionId, String challengeId, String studentId, 
                                     String code, String language) {
        try {
            // TODO: Replace with actual gRPC call to CodeRunner microservice
            // For now, use intelligent simulation based on code content
            List<String> approvedTestIds = simulateCodeExecution(code, language);
            
            return new ExecutionResult(true, approvedTestIds, 
                String.format("Code executed successfully. Language: %s, Tests passed: %d", 
                    language, approvedTestIds.size()));
            
        } catch (Exception e) {
            return new ExecutionResult(false, List.of(), 
                "Error executing code: " + e.getMessage());
        }
    }

    private List<String> simulateCodeExecution(String code, String language) {
        // Intelligent simulation based on code content
        if (code == null || code.trim().isEmpty()) {
            return List.of(); // No tests pass for empty code
        }
        
        // Simulate different test results based on code complexity and content
        if (code.contains("return") && code.length() > 50 && 
            (code.contains("for") || code.contains("while") || code.contains("if"))) {
            // Complex code with control structures
            return List.of("test_1", "test_2", "test_3", "test_4", "test_5"); 
        } else if (code.contains("return") && code.length() > 30) {
            // Medium complexity code
            return List.of("test_1", "test_2", "test_3"); 
        } else if (code.contains("System.out.println") || code.contains("print")) {
            // Basic output code
            return List.of("test_1", "test_2"); 
        } else if (code.contains("return")) {
            // Simple return statement
            return List.of("test_1"); 
        } else {
            // Basic code without return
            return List.of(); // No tests pass
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