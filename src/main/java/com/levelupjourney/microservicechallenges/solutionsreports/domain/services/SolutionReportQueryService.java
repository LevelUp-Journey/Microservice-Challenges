package com.levelupjourney.microservicechallenges.solutionsreports.domain.services;

import com.levelupjourney.microservicechallenges.solutionsreports.domain.model.aggregates.SolutionReport;
import com.levelupjourney.microservicechallenges.solutionsreports.domain.model.queries.GetReportsBySolutionIdQuery;
import com.levelupjourney.microservicechallenges.solutionsreports.domain.model.queries.GetReportsByStudentIdQuery;
import com.levelupjourney.microservicechallenges.solutionsreports.domain.model.queries.GetSolutionReportByIdQuery;

import java.util.List;
import java.util.Optional;

public interface SolutionReportQueryService {
    Optional<SolutionReport> handle(GetReportsBySolutionIdQuery query);
    List<SolutionReport> handle(GetReportsByStudentIdQuery query);
    Optional<SolutionReport> handle(GetSolutionReportByIdQuery query);
}
