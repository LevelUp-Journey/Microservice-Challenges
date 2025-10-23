package com.levelupjourney.microservicechallenges.solutions.domain.model.commands;

import com.levelupjourney.microservicechallenges.solutions.domain.model.valueobjects.SolutionId;

/**
 * Command to update a solution's code
 * Only the student's code can be updated
 */
public record UpdateSolutionCommand(SolutionId solutionId, String code) {
}
