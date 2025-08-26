package com.levelupjourney.microservicechallenges.challenge.domain.model.aggregates;

import com.levelupjourney.microservicechallenges.challenge.domain.model.entities.ChallengeVersion;
import com.levelupjourney.microservicechallenges.challenge.domain.model.valueobjects.ChallengeId;
import com.levelupjourney.microservicechallenges.challenge.domain.model.valueobjects.Star;

public class Challenge {
    private ChallengeId id;
    private String title;
    private String description;

    private Star[] stars;
    private ChallengeVersion[] versions;
}
