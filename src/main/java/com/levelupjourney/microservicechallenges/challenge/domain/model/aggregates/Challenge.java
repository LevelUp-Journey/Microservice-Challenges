package com.levelupjourney.microservicechallenges.challenge.domain.model.aggregates;

import com.levelupjourney.microservicechallenges.challenge.domain.model.entities.ChallengeVersion;
import com.levelupjourney.microservicechallenges.challenge.domain.model.valueobjects.ChallengeState;
import com.levelupjourney.microservicechallenges.challenge.domain.model.valueobjects.Star;
import com.levelupjourney.microservicechallenges.challenge.domain.model.valueobjects.TeacherId;
import com.levelupjourney.microservicechallenges.shared.domain.model.valueobjects.ChallengeId;
import jakarta.persistence.*;
import java.util.List;

@Entity
public class Challenge {

    @EmbeddedId
    private ChallengeId id;

    @Embedded
    private TeacherId teacherId;

    @Column(nullable = false)
    private String title;

    private String description;

    @ElementCollection
    @CollectionTable(
        name = "challenge_stars",
        joinColumns = @JoinColumn(name = "challenge_id")
    )
    private List<Star> stars;

    @ElementCollection
    @CollectionTable(
        name = "challenge_versions",
        joinColumns = @JoinColumn(name = "challenge_id")
    )
    private List<ChallengeVersion> versions;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChallengeState state;
}
