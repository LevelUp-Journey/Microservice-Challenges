package com.levelupjourney.microservicechallenges.solutions.domain.model.commands;

import com.levelupjourney.microservicechallenges.solutions.domain.model.valueobjects.ChallengeId;
import com.levelupjourney.microservicechallenges.solutions.domain.model.valueobjects.StudentId;
import com.levelupjourney.microservicechallenges.solutions.domain.model.valueobjects.SolutionId;

public record SubmitSolutionCommand(
        SolutionId solutionId,
        ChallengeId challengeId, 
        String code,
        StudentId studentId,
        String language,
        String comments
) {
}
