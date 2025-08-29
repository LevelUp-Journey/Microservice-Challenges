package com.levelupjourney.microservicechallenges.challenge.domain.model.commands;

import com.levelupjourney.microservicechallenges.shared.domain.model.valueobjects.TestId;

public record RemoveTestFromChallengeCommand(
        TestId testId
) {
}
