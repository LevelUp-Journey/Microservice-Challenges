package com.levelupjourney.microservicechallenges.challenges.interfaces.rest.transform;

import com.levelupjourney.microservicechallenges.challenges.domain.model.commands.UpdateChallengeCommand;
import com.levelupjourney.microservicechallenges.challenges.domain.model.aggregates.Tag;
import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.ChallengeId;
import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.TagId;
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
            Optional.empty() // TODO: Implementar asignación de tags existentes por ID
        );
    }
}