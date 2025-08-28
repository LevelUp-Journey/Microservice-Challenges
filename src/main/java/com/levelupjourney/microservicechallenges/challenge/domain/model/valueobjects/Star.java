package com.levelupjourney.microservicechallenges.challenge.domain.model.valueobjects;

import com.levelupjourney.microservicechallenges.shared.domain.model.valueobjects.ChallengeId;
import com.levelupjourney.microservicechallenges.shared.domain.model.valueobjects.StudentId;
import jakarta.persistence.*;

@Embeddable
public record Star(
    @Embedded
    StudentId userId,
    
    @Embedded
    ChallengeId challengeId
) {
}
