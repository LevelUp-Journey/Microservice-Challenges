package com.levelupjourney.microservicechallenges.solutions.interfaces.rest.transform;

import com.levelupjourney.microservicechallenges.solutions.domain.model.commands.SubmitSolutionCommand;
import com.levelupjourney.microservicechallenges.solutions.domain.model.valueobjects.SolutionId;
import com.levelupjourney.microservicechallenges.solutions.interfaces.rest.resource.SubmitSolutionResource;

import java.util.UUID;

public class SubmitSolutionCommandFromResourceAssembler {

    public static SubmitSolutionCommand toCommandFromResource(String solutionId, SubmitSolutionResource resource) {
        return new SubmitSolutionCommand(
            new SolutionId(UUID.fromString(solutionId)),
            resource.code()
        );
    }
}