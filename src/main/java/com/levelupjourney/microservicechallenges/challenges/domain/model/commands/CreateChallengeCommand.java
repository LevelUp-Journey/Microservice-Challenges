package com.levelupjourney.microservicechallenges.challenges.domain.model.commands;

import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.Difficulty;
import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.TeacherId;

import java.util.List;

public record CreateChallengeCommand(
    TeacherId teacherId, 
    String name, 
    String description, 
    Integer experiencePoints, 
    Difficulty difficulty,
    List<String> tagIds
) {
}
