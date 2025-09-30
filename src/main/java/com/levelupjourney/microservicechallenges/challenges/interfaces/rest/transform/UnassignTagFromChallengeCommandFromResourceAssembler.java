package com.levelupjourney.microservicechallenges.challenges.interfaces.rest.transform;

import com.levelupjourney.microservicechallenges.challenges.domain.model.commands.UnassignTagFromChallengeCommand;
import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.ChallengeId;
import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.TagId;

import java.util.UUID;

public class UnassignTagFromChallengeCommandFromResourceAssembler {
    
    public static UnassignTagFromChallengeCommand toCommandFromResource(String challengeId, String tagId) {
        return new UnassignTagFromChallengeCommand(
                new ChallengeId(UUID.fromString(challengeId)),
                new TagId(UUID.fromString(tagId))
        );
    }
}