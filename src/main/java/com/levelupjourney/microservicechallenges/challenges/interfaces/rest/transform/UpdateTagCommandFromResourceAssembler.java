package com.levelupjourney.microservicechallenges.challenges.interfaces.rest.transform;

import com.levelupjourney.microservicechallenges.challenges.domain.model.commands.UpdateTagCommand;
import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.TagId;
import com.levelupjourney.microservicechallenges.challenges.interfaces.rest.resource.UpdateTagResource;

import java.util.UUID;

public class UpdateTagCommandFromResourceAssembler {
    
    public static UpdateTagCommand toCommandFromResource(String tagId, UpdateTagResource resource) {
        return new UpdateTagCommand(
                new TagId(UUID.fromString(tagId)),
                resource.name(),
                resource.color(),
                resource.iconUrl()
        );
    }
}