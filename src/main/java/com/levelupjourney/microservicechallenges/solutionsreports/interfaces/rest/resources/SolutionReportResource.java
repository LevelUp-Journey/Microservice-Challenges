package com.levelupjourney.microservicechallenges.solutionsreports.interfaces.rest.resources;

import java.util.Date;
import java.util.List;

public record SolutionReportResource(
        String id,
        String solutionId,
        String studentId,
        List<String> successfulTestIds,
        Double timeTaken,
        Double memoryUsed,
        Date createdAt,
        Date updatedAt
) {
}