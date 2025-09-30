package com.levelupjourney.microservicechallenges.challenges.interfaces.rest.transform;

import com.levelupjourney.microservicechallenges.challenges.domain.model.commands.CreateTagCommand;
import com.levelupjourney.microservicechallenges.challenges.interfaces.rest.resource.CreateTagResource;

public class CreateTagCommandFromResourceAssembler {
    
    public static CreateTagCommand toCommandFromResource(CreateTagResource resource) {
        return new CreateTagCommand(
                resource.name(),
                resource.color(),
                resource.iconUrl()
        );
    }
}