package com.levelupjourney.microservicechallenges.challenges.domain.model.commands;

import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.TeacherId;

public record CreateChallengeCommand(TeacherId teacherId, String name, String description, Integer experiencePoints) {
}
