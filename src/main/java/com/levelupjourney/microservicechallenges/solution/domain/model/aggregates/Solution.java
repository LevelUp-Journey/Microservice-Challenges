package com.levelupjourney.microservicechallenges.solution.domain.model.aggregates;

import com.levelupjourney.microservicechallenges.shared.domain.model.valueobjects.Language;
import com.levelupjourney.microservicechallenges.shared.domain.model.valueobjects.ChallengeId;
import com.levelupjourney.microservicechallenges.shared.domain.model.valueobjects.StudentId;
import com.levelupjourney.microservicechallenges.solution.domain.model.entities.PassedTests;
import com.levelupjourney.microservicechallenges.solution.domain.model.valueobjects.SolutionId;

public class Solution {
    private SolutionId id;
    private StudentId studentId;
    private ChallengeId challengeId;
    private Language language;

    private String code;
    private PassedTests passedTests;
}
