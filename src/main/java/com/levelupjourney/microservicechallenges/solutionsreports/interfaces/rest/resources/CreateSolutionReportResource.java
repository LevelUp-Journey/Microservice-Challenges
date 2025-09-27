package com.levelupjourney.microservicechallenges.solutionsreports.interfaces.rest.resources;

import java.util.List;

public record CreateSolutionReportResource(
        String solutionId,
        String studentId,
        List<String> successfulTestIds,
        Double timeTaken,
        Double memoryUsed
) {
}