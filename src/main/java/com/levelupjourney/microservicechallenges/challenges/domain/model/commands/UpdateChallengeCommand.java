package com.levelupjourney.microservicechallenges.challenges.domain.model.commands;

import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.ChallengeId;
import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.ChallengeStatus;
import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.Difficulty;

import java.util.List;
import java.util.Optional;

public record UpdateChallengeCommand(
    ChallengeId challengeId, 
    Optional<String> name, 
    Optional<String> description, 
    Optional<Integer> experiencePoints, 
    Optional<Difficulty> difficulty, 
    Optional<ChallengeStatus> status, 
    Optional<List<String>> tags
) {
}
