package com.levelupjourney.microservicechallenges.solutionsreports.interfaces.rest.transform;

import com.levelupjourney.microservicechallenges.solutionsreports.domain.model.aggregates.SolutionReport;
import com.levelupjourney.microservicechallenges.solutionsreports.interfaces.rest.resources.SolutionReportResource;

import java.util.stream.Collectors;

public class SolutionReportResourceFromEntityAssembler {
    
    public static SolutionReportResource toResourceFromEntity(SolutionReport entity) {
        return new SolutionReportResource(
                entity.getId().id().toString(),
                entity.getSolutionId().id().toString(),
                entity.getStudentId().id().toString(),
                entity.getSuccessfulTests() != null ? 
                    entity.getSuccessfulTests().stream()
                            .map(testId -> testId.id().toString())
                            .collect(Collectors.toList()) : null,
                entity.getTimeTaken(),
                entity.getMemoryUsed(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}