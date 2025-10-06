package com.levelupjourney.microservicechallenges.challenges.domain.model.commands;

import com.levelupjourney.microservicechallenges.challenges.domain.model.aggregates.Tag;
import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.ChallengeId;
import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.ChallengeStatus;

import java.util.List;
import java.util.Optional;

public record UpdateChallengeCommand(ChallengeId challengeId, Optional<String> name, Optional<String> description, Optional<Integer> experiencePoints, Optional<ChallengeStatus> status, Optional<List<Tag>> tags) {
}
