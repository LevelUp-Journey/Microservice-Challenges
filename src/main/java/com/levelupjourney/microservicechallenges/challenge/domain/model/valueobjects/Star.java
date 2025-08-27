package com.levelupjourney.microservicechallenges.challenge.domain.model.valueobjects;

import com.levelupjourney.microservicechallenges.shared.domain.model.valueobjects.ChallengeId;
import com.levelupjourney.microservicechallenges.shared.domain.model.valueobjects.StudentId;

public record Star(StudentId userId, ChallengeId challengeId) {
}
