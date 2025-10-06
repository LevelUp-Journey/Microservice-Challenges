package com.levelupjourney.microservicechallenges.solutions.domain.model.commands;

import com.levelupjourney.microservicechallenges.solutions.domain.model.valueobjects.SolutionId;
import com.levelupjourney.microservicechallenges.solutions.domain.model.valueobjects.StudentId;

public record SubmitSolutionCommand(
        SolutionId solutionId,
        String code,
        StudentId studentId
) {
}
