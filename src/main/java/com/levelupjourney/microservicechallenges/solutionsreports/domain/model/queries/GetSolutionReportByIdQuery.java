package com.levelupjourney.microservicechallenges.solutionsreports.domain.model.queries;

import com.levelupjourney.microservicechallenges.solutionsreports.domain.model.valueobjects.SolutionReportId;

public record GetSolutionReportByIdQuery(SolutionReportId reportId) {
}