package com.levelupjourney.microservicechallenges.challenges.domain.model.aggregates;

import com.levelupjourney.microservicechallenges.challenges.domain.model.entities.ChallengeTag;
import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.ChallengeId;
import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.ChallengeStatus;
import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.TeacherId;
import com.levelupjourney.microservicechallenges.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.Entity;
import lombok.Getter;

import java.util.List;

@Getter
@Entity
public class Challenge extends AuditableAbstractAggregateRoot<Challenge> {
    private ChallengeId id;
    private TeacherId teacherId;
    private String name;
    private String description;
    private Integer experiencePoints;
    private ChallengeStatus status;
    private List<CodeVersion> versions;
    private List<ChallengeTag> tags;
}
