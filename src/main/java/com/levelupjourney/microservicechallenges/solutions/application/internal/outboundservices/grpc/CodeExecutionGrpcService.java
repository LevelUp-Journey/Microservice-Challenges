package com.levelupjourney.microservicechallenges.solutions.application.internal.outboundservices.grpc;

import com.levelupjourney.microservicechallenges.solutions.interfaces.grpc.CodeExecutionServiceGrpc;
import com.levelupjourney.microservicechallenges.solutions.interfaces.grpc.ExecutionRequest;
import com.levelupjourney.microservicechallenges.solutions.interfaces.grpc.ExecutionResponse;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CodeExecutionGrpcService {

    @GrpcClient("code-runner")
    private CodeExecutionServiceGrpc.CodeExecutionServiceBlockingStub codeExecutionStub;

    public ExecutionResult executeCode(String solutionId, String challengeId, String studentId, 
                                     String code, String language) {
        try {
            // Build the gRPC request
            ExecutionRequest request = ExecutionRequest.newBuilder()
                .setSolutionId(solutionId)
                .setChallengeId(challengeId)
                .setStudentId(studentId)
                .setCode(code)
                .setLanguage(language)
                .build();

            // Make the gRPC call
            ExecutionResponse response = codeExecutionStub.executeCode(request);

            // Return the result
            return new ExecutionResult(
                response.getSuccess(),
                response.getApprovedTestIdsList(),
                response.getMessage()
            );
            
        } catch (Exception e) {
            return new ExecutionResult(false, List.of(), 
                "Error executing code via gRPC: " + e.getMessage());
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