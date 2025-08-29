package com.levelupjourney.microservicechallenges.challenge.domain.model.commands;

import com.levelupjourney.microservicechallenges.challenge.domain.model.valueobjects.TeacherId;

public record CreateChallengeCommand(
        TeacherId teacherId,
        String title,
        String description
) {
}
