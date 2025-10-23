package com.levelupjourney.microservicechallenges.challenges.interfaces.rest.transform;

import com.levelupjourney.microservicechallenges.challenges.domain.model.aggregates.Challenge;
import com.levelupjourney.microservicechallenges.challenges.interfaces.rest.resource.ChallengeResource;
import com.levelupjourney.microservicechallenges.challenges.interfaces.rest.resource.StarResource;

import java.util.stream.Collectors;

public class ChallengeResourceFromEntityAssembler {

    public static ChallengeResource toResourceFromEntity(Challenge entity) {
        return new ChallengeResource(
            entity.getId().id().toString(),
            entity.getTeacherId().id().toString(),
            entity.getName(),
            entity.getDescription(),
            entity.getExperiencePoints(),
            entity.getDifficulty().name(),
            entity.getStatus().name(),
            entity.getTags(), // Already List<String>
            entity.getStars().stream()
                .map(star -> new StarResource(star.getUserId(), star.getStarredAt()))
                .collect(Collectors.toList())
        );
    }
}