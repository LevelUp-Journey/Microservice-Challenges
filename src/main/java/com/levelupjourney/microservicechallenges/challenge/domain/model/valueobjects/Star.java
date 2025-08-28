package com.levelupjourney.microservicechallenges.challenge.domain.model.valueobjects;

import com.levelupjourney.microservicechallenges.shared.domain.model.valueobjects.ChallengeId;
import com.levelupjourney.microservicechallenges.shared.domain.model.valueobjects.StudentId;
import jakarta.persistence.Embeddable;

@Embeddable
public record Star(@jakarta.persistence.Embedded StudentId userId, @jakarta.persistence.Embedded ChallengeId challengeId) {
}
