package com.levelupjourney.microservicechallenges.challenge.domain.model.commands;

import com.levelupjourney.microservicechallenges.shared.domain.model.valueobjects.ChallengeId;
import com.levelupjourney.microservicechallenges.shared.domain.model.valueobjects.Language;

public record CreateChallengeVersionCommand(
        ChallengeId challengeId,
        Language language,
        String defaultStudentCode
) {
}
