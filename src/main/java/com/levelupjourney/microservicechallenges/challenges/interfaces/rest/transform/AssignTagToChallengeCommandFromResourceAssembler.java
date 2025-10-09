package com.levelupjourney.microservicechallenges.challenges.interfaces.rest.transform;

import com.levelupjourney.microservicechallenges.challenges.domain.model.commands.AssignTagToChallengeCommand;
import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.ChallengeId;
import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.TagId;

import java.util.UUID;

public class AssignTagToChallengeCommandFromResourceAssembler {
    
    public static AssignTagToChallengeCommand toCommandFromResource(String challengeId, String tagId) {
        return new AssignTagToChallengeCommand(
                new ChallengeId(UUID.fromString(challengeId)),
                new TagId(UUID.fromString(tagId))
        );
    }
}