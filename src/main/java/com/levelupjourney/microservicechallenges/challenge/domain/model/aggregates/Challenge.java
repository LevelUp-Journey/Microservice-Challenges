package com.levelupjourney.microservicechallenges.challenge.domain.model.aggregates;

import com.levelupjourney.microservicechallenges.challenge.domain.model.entities.ChallengeVersion;
import com.levelupjourney.microservicechallenges.challenge.domain.model.valueobjects.Star;

import java.util.UUID;

public class Challenge {
    private UUID id;
    private String title;
    private String description;

    private Star[] stars;
    private ChallengeVersion[] versions;
}
