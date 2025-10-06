package com.levelupjourney.microservicechallenges.challenges.domain.model.commands;

public record StartChallengeResult(
    String solutionId,
    boolean isNewSolution
) {
}
