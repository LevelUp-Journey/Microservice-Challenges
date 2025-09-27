package com.levelupjourney.microservicechallenges.solutionsreports.interfaces.rest.transform;

import com.levelupjourney.microservicechallenges.solutionsreports.domain.model.aggregates.SolutionReport;
import com.levelupjourney.microservicechallenges.solutionsreports.interfaces.rest.resources.SolutionReportResource;

import java.util.stream.Collectors;

public class SolutionReportResourceFromEntityAssembler {
    
    public static SolutionReportResource toResourceFromEntity(SolutionReport entity) {
        return new SolutionReportResource(
                entity.getId().value().toString(),
                entity.getSolutionId().value().toString(),
                entity.getStudentId().value().toString(),
                entity.getSuccessfulTests() != null ? 
                    entity.getSuccessfulTests().stream()
                            .map(testId -> testId.value().toString())
                            .collect(Collectors.toList()) : null,
                entity.getTimeTaken(),
                entity.getMemoryUsed(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}