package com.levelupjourney.microservicechallenges.challenges.interfaces.rest.transform;

import com.levelupjourney.microservicechallenges.challenges.domain.model.commands.UpdateChallengeCommand;
import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.ChallengeId;
import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.ChallengeStatus;
import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.Difficulty;
import com.levelupjourney.microservicechallenges.challenges.interfaces.rest.resource.UpdateChallengeResource;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UpdateChallengeCommandFromResourceAssembler {

    public static UpdateChallengeCommand toCommandFromResource(String challengeId, UpdateChallengeResource resource) {
        // Convert status string to ChallengeStatus enum if present
        Optional<ChallengeStatus> status = resource.status()
            .map(statusStr -> ChallengeStatus.valueOf(statusStr.toUpperCase()));

        // Convert difficulty string to Difficulty enum if present
        Optional<Difficulty> difficulty = resource.difficulty()
            .map(difficultyStr -> Difficulty.valueOf(difficultyStr.toUpperCase()));
        
        // Normalize tags: lowercase and ensure # prefix (only if tags are present)
        Optional<List<String>> tags = resource.tags()
            .map(tagList -> tagList.stream()
                .map(tag -> tag.startsWith("#") ? tag.toLowerCase() : "#" + tag.toLowerCase())
                .toList());
        
        return new UpdateChallengeCommand(
            new ChallengeId(UUID.fromString(challengeId)),
            resource.name(),
            resource.description(),
            resource.experiencePoints(),
            difficulty,
            status,
            tags
        );
    }
}