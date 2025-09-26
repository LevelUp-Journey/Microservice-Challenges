package com.levelupjourney.microservicechallenges.challenges.interfaces.rest.transform;

import com.levelupjourney.microservicechallenges.challenges.domain.model.aggregates.Challenge;
import com.levelupjourney.microservicechallenges.challenges.domain.model.entities.ChallengeTag;
import com.levelupjourney.microservicechallenges.challenges.interfaces.rest.resource.ChallengeResource;

import java.util.stream.Collectors;

public class ChallengeResourceFromEntityAssembler {

    public static ChallengeResource toResourceFromEntity(Challenge entity) {
        return new ChallengeResource(
            entity.getId().toString(),
            entity.getTeacherId().value().toString(),
            entity.getName(),
            entity.getDescription(),
            entity.getExperiencePoints(),
            entity.getStatus().name(),
            entity.getTags().stream()
                .map(ChallengeTag::name)
                .collect(Collectors.toList())
        );
    }
}