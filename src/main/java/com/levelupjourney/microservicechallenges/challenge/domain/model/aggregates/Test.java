package com.levelupjourney.microservicechallenges.challenge.domain.model.aggregates;

import com.levelupjourney.microservicechallenges.shared.domain.model.valueobjects.TestId;
import com.levelupjourney.microservicechallenges.challenge.domain.model.entities.ChallengeVersion;
import jakarta.persistence.*;

@Entity
public class Test {

    @EmbeddedId
    private TestId id;

    private String title;
    private String hint;
    private String onErrorHint;

    private String testCode;

    private String input;

    private String expectedOutput;

    @ManyToOne
    @JoinColumn(name = "challenge_version_id", nullable = false)
    private ChallengeVersion challengeVersion;
}
