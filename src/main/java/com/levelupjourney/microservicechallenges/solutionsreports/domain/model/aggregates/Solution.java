package com.levelupjourney.microservicechallenges.solutionsreports.domain.model.aggregates;

import com.levelupjourney.microservicechallenges.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import com.levelupjourney.microservicechallenges.solutionsreports.domain.model.valueobjects.*;

public class Solution extends AuditableAbstractAggregateRoot<Solution> {
    private SolutionId id;
    private ChallengeId challengeId;
    private CodeVersionId codeVersionId;
    private StudentId studentId;
    private SolutionDetails details;
}
