package com.levelupjourney.microservicechallenges.solution.domain.model.commands;

import com.levelupjourney.microservicechallenges.shared.domain.model.valueobjects.StudentId;
import com.levelupjourney.microservicechallenges.shared.domain.model.valueobjects.ChallengeId;
import com.levelupjourney.microservicechallenges.shared.domain.model.valueobjects.Language;

public record CreateSolutionCommand(
        StudentId studentId,
        ChallengeId challengeId,
        Language language,
        String code
) {
}
