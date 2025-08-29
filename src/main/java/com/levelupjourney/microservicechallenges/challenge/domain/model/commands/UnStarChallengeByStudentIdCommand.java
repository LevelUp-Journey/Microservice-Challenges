package com.levelupjourney.microservicechallenges.challenge.domain.model.commands;

import com.levelupjourney.microservicechallenges.shared.domain.model.valueobjects.ChallengeId;
import com.levelupjourney.microservicechallenges.shared.domain.model.valueobjects.StudentId;

public record UnStarChallengeByStudentIdCommand(
        ChallengeId challengeId,
        StudentId studentId
) {
}
