package com.levelupjourney.microservicechallenges.solution.domain.model.aggregates;

import com.levelupjourney.microservicechallenges.shared.domain.model.valueobjects.Language;
import com.levelupjourney.microservicechallenges.shared.domain.model.valueobjects.ChallengeId;
import com.levelupjourney.microservicechallenges.shared.domain.model.valueobjects.StudentId;
import com.levelupjourney.microservicechallenges.solution.domain.model.entities.PassedTests;
import com.levelupjourney.microservicechallenges.solution.domain.model.valueobjects.SolutionId;
import jakarta.persistence.*;

@Entity
public class Solution {

    @EmbeddedId
    private SolutionId id;

    @Embedded
    private StudentId studentId;

    @Embedded
    private ChallengeId challengeId;

    @Enumerated(EnumType.STRING)
    private Language language;

    private String code;

    @Embedded
    private PassedTests passedTests;
}
