package com.levelupjourney.microservicechallenges.solutions.domain.model.commands;

import com.levelupjourney.microservicechallenges.solutions.domain.model.valueobjects.SolutionId;

public record UpdateSolutionCommand(SolutionId solutionId, String code) {
}
