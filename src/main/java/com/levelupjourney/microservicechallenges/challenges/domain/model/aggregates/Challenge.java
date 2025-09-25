package com.levelupjourney.microservicechallenges.challenges.domain.model.aggregates;

import com.levelupjourney.microservicechallenges.challenges.domain.model.entities.ChallengeTag;
import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.ChallengeStatus;
import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.TeacherId;
import com.levelupjourney.microservicechallenges.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Entity
public class Challenge extends AuditableAbstractAggregateRoot<Challenge> {
    
    @Id
    private UUID id;

    @Embedded
    private TeacherId teacherId;

    @Column(nullable = false)
    private String name;
    
    @Column(length = 1000)
    private String description;
    
    private Integer experiencePoints;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChallengeStatus status;

    @OneToMany(mappedBy = "challengeId", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<CodeVersion> versions = new ArrayList<>();
    
    @OneToMany(mappedBy = "challengeId", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ChallengeTag> tags = new ArrayList<>();
}
