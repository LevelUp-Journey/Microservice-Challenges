package com.levelupjourney.microservicechallenges.challenges.interfaces.rest.transform;

import com.levelupjourney.microservicechallenges.challenges.domain.model.commands.CreateChallengeCommand;
import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.Difficulty;
import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.TeacherId;
import com.levelupjourney.microservicechallenges.challenges.interfaces.rest.resource.CreateChallengeResource;

import java.util.UUID;

public class CreateChallengeCommandFromResourceAssembler {

    public static CreateChallengeCommand toCommandFromResource(CreateChallengeResource resource) {
        return new CreateChallengeCommand(
            new TeacherId(UUID.fromString(resource.teacherId())),
            resource.name(),
            resource.description(),
            resource.experiencePoints(),
            Difficulty.valueOf(resource.difficulty().toUpperCase())
        );
    }
}
