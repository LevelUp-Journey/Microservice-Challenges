package com.levelupjourney.microservicechallenges.solutions.interfaces.rest.transform;

import com.levelupjourney.microservicechallenges.solutions.domain.model.commands.UpdateSolutionCommand;
import com.levelupjourney.microservicechallenges.solutions.domain.model.valueobjects.SolutionId;
import com.levelupjourney.microservicechallenges.solutions.interfaces.rest.resource.UpdateSolutionResource;

import java.util.UUID;

public class UpdateSolutionCommandFromResourceAssembler {

    public static UpdateSolutionCommand toCommandFromResource(String solutionId, UpdateSolutionResource resource) {
        return new UpdateSolutionCommand(
            new SolutionId(UUID.fromString(solutionId)),
            resource.code(),
            resource.language() != null ? resource.language() : "java" // default language
        );
    }
}