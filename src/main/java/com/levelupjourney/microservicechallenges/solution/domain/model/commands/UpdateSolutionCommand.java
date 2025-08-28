package com.levelupjourney.microservicechallenges.solution.domain.model.commands;

import com.levelupjourney.microservicechallenges.solution.domain.model.valueobjects.SolutionId;
import com.levelupjourney.microservicechallenges.shared.domain.model.valueobjects.Language;

public record UpdateSolutionCommand(
        SolutionId solutionId,
        String code,
        Language language
) {
}
