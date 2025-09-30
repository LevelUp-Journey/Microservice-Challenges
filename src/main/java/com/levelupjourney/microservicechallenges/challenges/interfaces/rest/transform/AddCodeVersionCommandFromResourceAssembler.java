package com.levelupjourney.microservicechallenges.challenges.interfaces.rest.transform;

import com.levelupjourney.microservicechallenges.challenges.domain.model.commands.AddCodeVersionCommand;
import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.ChallengeId;
import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.CodeLanguage;
import com.levelupjourney.microservicechallenges.challenges.interfaces.rest.resource.AddCodeVersionResource;

import java.util.UUID;

public class AddCodeVersionCommandFromResourceAssembler {

    public static AddCodeVersionCommand toCommandFromResource(AddCodeVersionResource resource) {
        return new AddCodeVersionCommand(
            new ChallengeId(UUID.fromString(resource.challengeId())),
            CodeLanguage.valueOf(resource.language().toUpperCase()),
            resource.defaultCode()
        );
    }
}