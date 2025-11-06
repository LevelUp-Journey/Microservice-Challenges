package com.levelupjourney.microservicechallenges.challenges.interfaces.rest.transform;

import com.levelupjourney.microservicechallenges.challenges.domain.model.commands.AddCodeVersionTestCommand;
import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.CodeVersionId;
import com.levelupjourney.microservicechallenges.challenges.interfaces.rest.resource.AddCodeVersionTestResource;

import java.util.UUID;

public class AddCodeVersionTestCommandFromResourceAssembler {

    public static AddCodeVersionTestCommand toCommandFromResource(AddCodeVersionTestResource resource) {
        return new AddCodeVersionTestCommand(
            new CodeVersionId(UUID.fromString(resource.codeVersionId())),
            resource.input(),
            resource.expectedOutput(),
            resource.customValidationCode(),
            resource.failureMessage(),
            resource.isSecret()
        );
    }
}