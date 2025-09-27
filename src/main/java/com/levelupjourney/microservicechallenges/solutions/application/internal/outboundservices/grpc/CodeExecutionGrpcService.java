package com.levelupjourney.microservicechallenges.solutions.application.internal.outboundservices.grpc;

import org.springframework.stereotype.Service;

@Service
public class CodeExecutionGrpcService {

    public ExecutionResult executeCode(String solutionId, String challengeId, String studentId, 
                                     String code, String language, String comments) {
        // TODO: Implement actual gRPC call to CodeRunner microservice (Go)
        // This will communicate with the Go/Gin CodeRunner service
        
        // For now, simulate a successful execution
        return new ExecutionResult(true, java.util.UUID.randomUUID().toString(), "Code executed successfully");
    }

    // Result wrapper class
    public static class ExecutionResult {
        private final boolean success;
        private final String executionId;
        private final String message;

        public ExecutionResult(boolean success, String executionId, String message) {
            this.success = success;
            this.executionId = executionId;
            this.message = message;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getExecutionId() {
            return executionId;
        }

        public String getMessage() {
            return message;
        }
    }
}