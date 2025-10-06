package com.levelupjourney.microservicechallenges.challenges.interfaces.rest.transform;

import com.levelupjourney.microservicechallenges.challenges.domain.model.commands.UpdateCodeVersionTestCommand;
import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.CodeVersionTestId;
import com.levelupjourney.microservicechallenges.challenges.interfaces.rest.resource.UpdateCodeVersionTestResource;

import java.util.UUID;

public class UpdateCodeVersionTestCommandFromResourceAssembler {

    public static UpdateCodeVersionTestCommand toCommandFromResource(String testId, UpdateCodeVersionTestResource resource) {
        return new UpdateCodeVersionTestCommand(
            new CodeVersionTestId(UUID.fromString(testId)),
            resource.input(),
            resource.expectedOutput(),
            resource.customValidationCode(),
            resource.failureMessage()
        );
    }
}