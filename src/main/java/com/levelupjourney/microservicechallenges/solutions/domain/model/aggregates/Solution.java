package com.levelupjourney.microservicechallenges.solutions.domain.model.aggregates;

import com.levelupjourney.microservicechallenges.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import com.levelupjourney.microservicechallenges.solutions.domain.model.valueobjects.*;
import jakarta.persistence.Entity;
import lombok.Getter;


@Getter
@Entity
public class Solution extends AuditableAbstractAggregateRoot<Solution> {
    private SolutionId id;
    private ChallengeId challengeId;
    private CodeVersionId codeVersionId;
    private StudentId studentId;
    private SolutionDetails details;
}
