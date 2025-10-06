package com.levelupjourney.microservicechallenges.solutionsreports.interfaces.rest.transform;

import com.levelupjourney.microservicechallenges.solutionsreports.domain.model.queries.GetSolutionReportByIdQuery;
import com.levelupjourney.microservicechallenges.solutionsreports.domain.model.valueobjects.SolutionReportId;

import java.util.UUID;

public class GetSolutionReportByIdQueryFromResourceAssembler {
    
    public static GetSolutionReportByIdQuery toQueryFromReportId(String reportId) {
        return new GetSolutionReportByIdQuery(new SolutionReportId(UUID.fromString(reportId)));
    }
}