package com.levelupjourney.microservicechallenges.challenges.interfaces.rest.transform;

import com.levelupjourney.microservicechallenges.challenges.domain.model.commands.CreateChallengeCommand;
import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.Difficulty;
import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.TeacherId;
import com.levelupjourney.microservicechallenges.challenges.interfaces.rest.resource.CreateChallengeResource;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class CreateChallengeCommandFromResourceAssembler {

    public static CreateChallengeCommand toCommandFromResource(CreateChallengeResource resource) {
        // Normalize tags: lowercase and ensure they start with #
        List<String> tags = resource.tags() != null 
            ? resource.tags().stream()
                .map(tag -> tag.startsWith("#") ? tag.toLowerCase() : "#" + tag.toLowerCase())
                .toList()
            : Collections.emptyList();
        
        return new CreateChallengeCommand(
            new TeacherId(UUID.fromString(resource.teacherId())),
            resource.name(),
            resource.description(),
            resource.experiencePoints(),
            Difficulty.valueOf(resource.difficulty().toUpperCase()),
            tags
        );
    }
}
