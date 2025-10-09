package com.levelupjourney.microservicechallenges.solutionsreports.domain.model.commands;

import com.levelupjourney.microservicechallenges.solutionsreports.domain.model.valueobjects.SolutionReportId;

public record DeleteSolutionReportCommand(SolutionReportId reportId) {
}