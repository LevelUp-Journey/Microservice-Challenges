package com.levelupjourney.microservicechallenges.challenge.domain.model.commands;

import com.levelupjourney.microservicechallenges.shared.domain.model.valueobjects.TestId;

public record UpdateTestCommand(
        TestId testId,
        String title,
        String hint,
        String onErrorHint,
        String testCode,
        String input,
        String expectedOutput
) {
}
