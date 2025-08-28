package com.levelupjourney.microservicechallenges.challenge.domain.model.valueobjects;

import com.levelupjourney.microservicechallenges.shared.domain.model.valueobjects.ChallengeId;
import com.levelupjourney.microservicechallenges.shared.domain.model.valueobjects.StudentId;
import jakarta.persistence.*;

@Embeddable
public record Star(
    @Embedded
    @AttributeOverride(name = "id", column = @Column(name = "student_id", columnDefinition = "uuid"))
    StudentId userId, 
    
    @Embedded
    @AttributeOverride(name = "id", column = @Column(name = "challenge_id", columnDefinition = "uuid"))
    ChallengeId challengeId
) {
}
