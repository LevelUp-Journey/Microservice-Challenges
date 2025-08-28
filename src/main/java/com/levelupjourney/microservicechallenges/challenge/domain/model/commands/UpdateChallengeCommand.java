package com.levelupjourney.microservicechallenges.challenge.domain.model.commands;

import com.levelupjourney.microservicechallenges.shared.domain.model.valueobjects.ChallengeId;
import com.levelupjourney.microservicechallenges.challenge.domain.model.valueobjects.TeacherId;

import java.util.List;

public record UpdateChallengeCommand(
        ChallengeId challengeId,
        String title,
        String description,
        String difficulty,
        List<String> tags,
        TeacherId teacherId
) {
}
