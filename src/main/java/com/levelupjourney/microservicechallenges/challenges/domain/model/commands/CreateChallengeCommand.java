package com.levelupjourney.microservicechallenges.challenges.domain.model.commands;

import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.Difficulty;
import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.TeacherId;

import java.util.List;
import java.util.UUID;

public record CreateChallengeCommand(
    TeacherId teacherId, 
    String name, 
    String description, 
    Integer experiencePoints, 
    Difficulty difficulty,
    List<String> tags,
    List<UUID> guides,
    Integer maxAttemptsBeforeGuides
) {
}
