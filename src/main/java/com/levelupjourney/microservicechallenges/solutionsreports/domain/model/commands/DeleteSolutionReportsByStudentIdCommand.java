package com.levelupjourney.microservicechallenges.solutionsreports.domain.model.commands;

import com.levelupjourney.microservicechallenges.solutionsreports.domain.model.valueobjects.StudentId;

public record DeleteSolutionReportsByStudentIdCommand(StudentId studentId) {
}