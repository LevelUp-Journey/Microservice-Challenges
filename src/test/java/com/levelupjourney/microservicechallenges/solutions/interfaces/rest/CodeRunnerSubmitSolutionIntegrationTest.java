package com.levelupjourney.microservicechallenges.solutions.interfaces.rest;

import com.levelupjourney.microservicechallenges.solutions.application.internal.outboundservices.grpc.CodeRunnerExecutionService;
import com.levelupjourney.microservicechallenges.shared.interfaces.rest.resources.CodeVersionTestForSubmittingResource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test for the new CodeRunner gRPC implementation.
 * This test validates that the submit solution flow uses the new CodeRunnerExecutionService.
 */
@SpringBootTest
@TestPropertySource(properties = {
    "grpc.client.code-runner.address=static://localhost:9084",
    "grpc.client.code-runner.negotiation-type=plaintext",
    "grpc.client.code-runner.max-inbound-message-size=8MB"
})
public class CodeRunnerSubmitSolutionIntegrationTest {

    @Autowired
    private CodeRunnerExecutionService codeRunnerExecutionService;

    @Test
    public void contextLoads() {
        // Verify the new CodeRunnerExecutionService is properly injected
        assertNotNull(codeRunnerExecutionService, "CodeRunnerExecutionService should be injected");
    }

    @Test
    @Disabled("Requires CodeRunner microservice running on port 9084")
    public void testCodeRunnerExecutionService() {
        // Test data
        String codeVersionId = "test-code-version-123";
        String studentId = "test-student-456";
        String language = "java";
        String code = """
                public class Solution {
                    public int add(int a, int b) {
                        return a + b;
                    }
                }
                """;
        
        List<CodeVersionTestForSubmittingResource> tests = List.of(
            new CodeVersionTestForSubmittingResource(
                "test-1", 
                "2,3", 
                "5", 
                ""
            ),
            new CodeVersionTestForSubmittingResource(
                "test-2", 
                "5,7", 
                "12", 
                ""
            )
        );

        // Execute the solution
        var result = codeRunnerExecutionService.executeSolution(
            codeVersionId, studentId, language, code, tests
        );

        // Verify the response structure matches our specification
        assertNotNull(result, "Execution result should not be null");
        assertNotNull(result.passedTestsId(), "Passed tests ID list should not be null");
        assertTrue(result.timeTaken() >= 0, "Time taken should be non-negative");
        
        // Verify the successful flag is calculated correctly
        boolean expectedSuccess = result.passedTestsId().size() == tests.size();
        assertEquals(expectedSuccess, result.successful(), 
            "Successful flag should be true if all tests passed");

        System.out.println("CodeRunner execution result:");
        System.out.println("- Passed tests: " + result.passedTestsId());
        System.out.println("- Time taken: " + result.timeTaken() + " ms");
        System.out.println("- Successful: " + result.successful());
    }

    @Test
    @Disabled("Requires full application context and CodeRunner service")
    public void testSubmitSolutionEndpointWithCodeRunner() {
        /*
         * This test would validate the complete flow:
         * 
         * 1. Create a solution via POST /api/v1/solutions
         * 2. Submit the solution via POST /api/v1/solutions/{id}/submit
         * 3. Verify the response contains:
         *    - passedTestsId: List<String>
         *    - timeTaken: double
         *    - successful: boolean
         * 4. Verify that successful = true if passedTestsId.size() == totalTests
         */
    }
}