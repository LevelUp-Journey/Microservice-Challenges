package com.levelupjourney.microservicechallenges.challenges.interfaces.rest.transform;

import com.levelupjourney.microservicechallenges.challenges.domain.model.aggregates.Challenge;
import com.levelupjourney.microservicechallenges.challenges.domain.model.entities.ChallengeTag;
import com.levelupjourney.microservicechallenges.challenges.interfaces.rest.resource.ChallengeResource;

import java.util.stream.Collectors;

public class ChallengeResourceFromEntityAssembler {

    public static ChallengeResource toResourceFromEntity(Challenge entity) {
        return new ChallengeResource(
            entity.getId().id().toString(),
            entity.getTeacherId().id().toString(),
            entity.getName(),
            entity.getDescription(),
            entity.getExperiencePoints(),
            entity.getStatus().name(),
            entity.getTags().stream()
                .map(ChallengeTag::getName)
                .collect(Collectors.toList())
        );
    }
}