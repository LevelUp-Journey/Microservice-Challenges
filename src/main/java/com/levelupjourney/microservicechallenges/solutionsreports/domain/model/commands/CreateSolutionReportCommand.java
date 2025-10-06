package com.levelupjourney.microservicechallenges.solutionsreports.domain.model.commands;

import com.levelupjourney.microservicechallenges.solutionsreports.domain.model.valueobjects.CodeVersionTestId;
import com.levelupjourney.microservicechallenges.solutionsreports.domain.model.valueobjects.SolutionId;
import com.levelupjourney.microservicechallenges.solutionsreports.domain.model.valueobjects.StudentId;

import java.util.List;

public record CreateSolutionReportCommand(SolutionId solutionId, StudentId studentId, List<CodeVersionTestId> successfulTests, Double timeTaken, Double memoryUsed) {
}
