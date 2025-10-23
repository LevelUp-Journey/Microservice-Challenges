package com.levelupjourney.microservicechallenges.challenges.interfaces.rest.transform;

import com.levelupjourney.microservicechallenges.challenges.domain.model.commands.UpdateCodeVersionCommand;
import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.CodeVersionId;
import com.levelupjourney.microservicechallenges.challenges.interfaces.rest.resource.UpdateCodeVersionResource;

import java.util.UUID;

public class UpdateCodeVersionCommandFromResourceAssembler {

    public static UpdateCodeVersionCommand toCommandFromResource(String codeVersionId, UpdateCodeVersionResource resource) {
        return new UpdateCodeVersionCommand(
            new CodeVersionId(UUID.fromString(codeVersionId)),
            resource.code(),
            resource.functionName()
        );
    }
}