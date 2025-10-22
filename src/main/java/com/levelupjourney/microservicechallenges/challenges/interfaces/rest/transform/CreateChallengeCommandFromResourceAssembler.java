package com.levelupjourney.microservicechallenges.challenges.interfaces.rest.transform;

import com.levelupjourney.microservicechallenges.challenges.domain.model.commands.CreateChallengeCommand;
import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.Difficulty;
import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.TeacherId;
import com.levelupjourney.microservicechallenges.challenges.interfaces.rest.resource.CreateChallengeResource;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class CreateChallengeCommandFromResourceAssembler {

    public static CreateChallengeCommand toCommandFromResource(CreateChallengeResource resource, String teacherId) {
        // Use provided tagIds or empty list if not provided
        List<String> tagIds = resource.tagIds() != null ? resource.tagIds() : Collections.emptyList();
        
        return new CreateChallengeCommand(
            new TeacherId(UUID.fromString(teacherId)),
            resource.name(),
            resource.description(),
            resource.experiencePoints(),
            Difficulty.valueOf(resource.difficulty().toUpperCase()),
            tagIds
        );
    }
}
