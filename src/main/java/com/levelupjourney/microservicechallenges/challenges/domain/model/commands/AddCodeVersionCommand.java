package com.levelupjourney.microservicechallenges.challenges.domain.model.commands;

import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.ChallengeId;
import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.CodeLanguage;

public record AddCodeVersionCommand(ChallengeId challengeId, CodeLanguage language, String defaultCode) {
}
