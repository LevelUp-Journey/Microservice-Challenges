package com.levelupjourney.microservicechallenges.challenges.domain.model.commands;

import com.levelupjourney.microservicechallenges.challenges.domain.model.entities.ChallengeTag;
import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.ChallengeId;

import java.util.List;
import java.util.Optional;

public record UpdateChallengeCommand(ChallengeId challengeId, Optional<String> name, Optional<String> description, Optional<Integer> experiencePoints, Optional<List<ChallengeTag>> tags) {
}
