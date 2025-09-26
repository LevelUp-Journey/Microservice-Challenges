package com.levelupjourney.microservicechallenges.challenges.interfaces.rest.transform;

import com.levelupjourney.microservicechallenges.challenges.domain.model.commands.PublishChallengeCommand;
import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.ChallengeId;
import com.levelupjourney.microservicechallenges.challenges.interfaces.rest.resource.PublishChallengeResource;

import java.util.UUID;

public class PublishChallengeCommandFromResourceAssembler {

    public static PublishChallengeCommand toCommandFromResource(PublishChallengeResource resource) {
        return new PublishChallengeCommand(
            new ChallengeId(UUID.fromString(resource.challengeId()))
        );
    }
}