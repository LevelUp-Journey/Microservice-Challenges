package com.levelupjourney.microservicechallenges.solution.interfaces.rest.resources;

import java.util.List;

public record SolutionResource(
        String id,
        String studentId,
        String challengeId,
        String language,
        String code,
        List<String> passedTestIds,
        int passedTestsCount
) {
}
