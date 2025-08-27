package com.levelupjourney.microservicechallenges.solution.domain.model.aggregates;

import com.levelupjourney.microservicechallenges.shared.domain.model.valueobjects.TestId;
import com.levelupjourney.microservicechallenges.solution.domain.model.valueobjects.SolutionId;

public class Solution {
    private SolutionId id;
    private String code;
    private TestId[] passedTests;
}
