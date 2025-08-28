package com.levelupjourney.microservicechallenges.solution.domain.model.commands;

import com.levelupjourney.microservicechallenges.shared.domain.model.valueobjects.ChallengeId;
import com.levelupjourney.microservicechallenges.shared.domain.model.valueobjects.StudentId;

public record SubmitSolutionCommand(
        ChallengeId challengeId,
        StudentId studentId,
        String code,
        String language
) {
}
