package com.levelupjourney.microservicechallenges.challenges.interfaces.rest.transform;

import com.levelupjourney.microservicechallenges.challenges.domain.model.commands.UpdateChallengeCommand;
import com.levelupjourney.microservicechallenges.challenges.domain.model.entities.ChallengeTag;
import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.ChallengeId;
import com.levelupjourney.microservicechallenges.challenges.interfaces.rest.resource.UpdateChallengeResource;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class UpdateChallengeCommandFromResourceAssembler {

    public static UpdateChallengeCommand toCommandFromResource(String challengeId, UpdateChallengeResource resource) {
        return new UpdateChallengeCommand(
            new ChallengeId(UUID.fromString(challengeId)),
            resource.name(),
            resource.description(),
            resource.experiencePoints(),
            Optional.ofNullable(resource.tags()).map(tags ->
                tags.stream()
                    .map(tagName -> new ChallengeTag(tagName, null, null ))
                    .collect(Collectors.toList())
            )
        );
    }
}