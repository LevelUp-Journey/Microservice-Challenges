package com.levelupjourney.microservicechallenges.challenges.interfaces.rest.transform;

import com.levelupjourney.microservicechallenges.challenges.domain.model.commands.DeleteTagCommand;
import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.TagId;

import java.util.UUID;

public class DeleteTagCommandFromResourceAssembler {
    
    public static DeleteTagCommand toCommandFromResource(String tagId) {
        return new DeleteTagCommand(
                new TagId(UUID.fromString(tagId))
        );
    }
}