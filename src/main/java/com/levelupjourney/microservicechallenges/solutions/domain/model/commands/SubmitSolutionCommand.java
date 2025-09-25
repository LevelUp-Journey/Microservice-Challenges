package com.levelupjourney.microservicechallenges.solutions.domain.model.commands;

import com.levelupjourney.microservicechallenges.solutions.domain.model.valueobjects.SolutionId;

public record SubmitSolutionCommand(SolutionId solutionId, String code) {
}
