package com.levelupjourney.microservicechallenges.solutions.interfaces.rest.resource;

import java.util.List;

/**
 * Resource representing the final response sent to the client after code execution
 */
public record CodeExecutionResultResource(
        List<String> passedTestsId,
        double timeTaken,
        boolean successful
) {
}