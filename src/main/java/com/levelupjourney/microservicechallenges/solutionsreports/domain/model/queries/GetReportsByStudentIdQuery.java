package com.levelupjourney.microservicechallenges.solutionsreports.domain.model.queries;

import com.levelupjourney.microservicechallenges.solutionsreports.domain.model.valueobjects.StudentId;

public record GetReportsByStudentIdQuery(StudentId studentId) {
}