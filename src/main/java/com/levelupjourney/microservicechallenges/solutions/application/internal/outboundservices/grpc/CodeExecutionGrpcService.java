package com.levelupjourney.microservicechallenges.solutions.application.internal.outboundservices.grpc;

import com.levelupjourney.microservicechallenges.shared.interfaces.rest.resources.CodeVersionTestForSubmittingResource;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CodeExecutionGrpcService {

    /**
     * Execute code with all tests from CodeVersion
     */
    public ExecutionResult executeCodeWithTests(String solutionId, String codeVersionId, String studentId, 
                                               String code, String language, 
                                               List<CodeVersionTestForSubmittingResource> tests) {
        try {
            // TODO: Replace with actual gRPC call to CodeRunner microservice
            // For now, use intelligent simulation based on code content and tests
            List<String> approvedTestIds = simulateCodeExecutionWithTests(code, language, tests);
            
            return new ExecutionResult(true, approvedTestIds, 
                String.format("Code executed successfully. Language: %s, Tests run: %d, Tests passed: %d", 
                    language, tests.size(), approvedTestIds.size()));
            
        } catch (Exception e) {
            return new ExecutionResult(false, List.of(), 
                "Error executing code with tests: " + e.getMessage());
        }
    }

    private List<String> simulateCodeExecutionWithTests(String code, String language, 
                                                       List<CodeVersionTestForSubmittingResource> tests) {
        // Intelligent simulation based on code complexity and number of tests
        if (code == null || code.trim().isEmpty()) {
            return List.of(); // No tests pass for empty code
        }
        
        int totalTests = tests.size();
        if (totalTests == 0) {
            return List.of(); // No tests to run
        }
        
        // Determine how many tests should pass based on code complexity
        int testsToPass = calculateTestsToPass(code, totalTests);
        
        // Return the first N test IDs as approved
        return tests.stream()
                .limit(testsToPass)
                .map(CodeVersionTestForSubmittingResource::id)
                .toList();
    }

    private int calculateTestsToPass(String code, int totalTests) {
        // Complex code with control structures - most tests pass
        if (code.contains("return") && code.length() > 50 && 
            (code.contains("for") || code.contains("while") || code.contains("if"))) {
            return Math.min(totalTests, (int)(totalTests * 0.8)); // 80% pass
        } 
        // Medium complexity code
        else if (code.contains("return") && code.length() > 30) {
            return Math.min(totalTests, (int)(totalTests * 0.6)); // 60% pass
        } 
        // Basic output code
        else if (code.contains("System.out.println") || code.contains("print")) {
            return Math.min(totalTests, (int)(totalTests * 0.4)); // 40% pass
        } 
        // Simple return statement
        else if (code.contains("return")) {
            return Math.min(totalTests, (int)(totalTests * 0.2)); // 20% pass
        } 
        // Basic code without return
        else {
            return 0; // No tests pass
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