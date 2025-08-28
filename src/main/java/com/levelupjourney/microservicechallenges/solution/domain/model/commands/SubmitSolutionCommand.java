package com.levelupjourney.microservicechallenges.solution.domain.model.commands;

import com.levelupjourney.microservicechallenges.shared.domain.model.valueobjects.ChallengeId;
import com.levelupjourney.microservicechallenges.shared.domain.model.valueobjects.StudentId;
import com.levelupjourney.microservicechallenges.shared.domain.model.valueobjects.Language;

// For now this is a simple record
// In the future, we will integrate the Code Runner Microservice here
// TODO: Integrate Code Runner Microservice
public record SubmitSolutionCommand(
        ChallengeId challengeId,
        StudentId studentId,
        String code,
        Language language
) {
}
