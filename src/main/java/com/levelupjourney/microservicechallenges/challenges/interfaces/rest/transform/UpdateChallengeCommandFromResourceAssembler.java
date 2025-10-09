package com.levelupjourney.microservicechallenges.challenges.interfaces.rest.transform;

import com.levelupjourney.microservicechallenges.challenges.domain.model.commands.UpdateChallengeCommand;
import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.ChallengeId;
import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.ChallengeStatus;
import com.levelupjourney.microservicechallenges.challenges.interfaces.rest.resource.UpdateChallengeResource;

import java.util.Optional;
import java.util.UUID;

public class UpdateChallengeCommandFromResourceAssembler {

    public static UpdateChallengeCommand toCommandFromResource(String challengeId, UpdateChallengeResource resource) {
        // Convert status string to ChallengeStatus enum if present
        Optional<ChallengeStatus> status = resource.status()
            .map(statusStr -> ChallengeStatus.valueOf(statusStr.toUpperCase()));
        
        return new UpdateChallengeCommand(
            new ChallengeId(UUID.fromString(challengeId)),
            resource.name(),
            resource.description(),
            resource.experiencePoints(),
            status,
            Optional.empty() // TODO: Implementar asignación de tags existentes por ID
        );
    }
}