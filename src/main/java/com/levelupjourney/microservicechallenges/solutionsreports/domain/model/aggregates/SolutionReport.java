package com.levelupjourney.microservicechallenges.solutionsreports.domain.model.aggregates;

import com.levelupjourney.microservicechallenges.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import com.levelupjourney.microservicechallenges.solutionsreports.domain.model.valueobjects.CodeVersionTestId;
import com.levelupjourney.microservicechallenges.solutionsreports.domain.model.valueobjects.SolutionId;
import com.levelupjourney.microservicechallenges.solutionsreports.domain.model.valueobjects.SolutionReportId;
import com.levelupjourney.microservicechallenges.solutionsreports.domain.model.valueobjects.StudentId;

import java.util.List;

public class SolutionReport extends AuditableAbstractAggregateRoot<SolutionReport> {
    private SolutionReportId id;
    private SolutionId solutionId;
    private StudentId studentId;
    private List<CodeVersionTestId> successfulTests;
    private Double timeTaken;
    private Double memoryUsed;
}
