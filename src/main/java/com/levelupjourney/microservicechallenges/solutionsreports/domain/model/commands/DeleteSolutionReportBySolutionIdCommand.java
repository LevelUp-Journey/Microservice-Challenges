package com.levelupjourney.microservicechallenges.solutionsreports.domain.model.commands;

import com.levelupjourney.microservicechallenges.solutionsreports.domain.model.valueobjects.SolutionId;

public record DeleteSolutionReportBySolutionIdCommand(SolutionId solutionId) {
}