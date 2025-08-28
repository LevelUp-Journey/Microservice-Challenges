package com.levelupjourney.microservicechallenges.challenge.domain.model.aggregates;

import com.levelupjourney.microservicechallenges.challenge.domain.model.entities.ChallengeVersion;
import com.levelupjourney.microservicechallenges.challenge.domain.model.valueobjects.ChallengeState;
import com.levelupjourney.microservicechallenges.challenge.domain.model.valueobjects.Star;
import com.levelupjourney.microservicechallenges.challenge.domain.model.valueobjects.TeacherId;
import com.levelupjourney.microservicechallenges.shared.domain.model.valueobjects.ChallengeId;
import jakarta.persistence.*;
import jakarta.persistence.AttributeOverride;
import java.util.List;

@Entity
public class Challenge {

    @EmbeddedId
    private ChallengeId id;

    @Embedded
    @AttributeOverride(name = "id", column = @Column(name = "teacher_id"))
    private TeacherId teacherId;

    private String title;

    private String description;

    @ElementCollection
    @CollectionTable(
        name = "challenge_stars",
        joinColumns = @JoinColumn(name = "owner_challenge_id")
    )
    private List<Star> stars;

    @OneToMany(mappedBy = "challenge", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChallengeVersion> versions;

    @Enumerated(EnumType.STRING)
    private ChallengeState state;
}
