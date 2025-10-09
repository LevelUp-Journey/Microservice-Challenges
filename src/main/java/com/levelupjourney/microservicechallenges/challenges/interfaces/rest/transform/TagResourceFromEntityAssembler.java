package com.levelupjourney.microservicechallenges.challenges.interfaces.rest.transform;

import com.levelupjourney.microservicechallenges.challenges.domain.model.aggregates.Tag;
import com.levelupjourney.microservicechallenges.challenges.interfaces.rest.resource.TagResource;

public class TagResourceFromEntityAssembler {
    
    public static TagResource toResourceFromEntity(Tag entity) {
        return new TagResource(
                entity.getId().id().toString(),
                entity.getName(),
                entity.getColor(),
                entity.getIconUrl()
        );
    }
}