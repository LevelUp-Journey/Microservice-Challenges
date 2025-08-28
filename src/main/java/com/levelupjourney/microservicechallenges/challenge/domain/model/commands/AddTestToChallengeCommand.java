package com.levelupjourney.microservicechallenges.challenge.domain.model.commands;

public record AddTestToChallengeCommand(
        Long challengeVersionId,
        String title,
        String hint,
        String onErrorHint,
        String testCode,
        String input,
        String expectedOutput
) {
}
