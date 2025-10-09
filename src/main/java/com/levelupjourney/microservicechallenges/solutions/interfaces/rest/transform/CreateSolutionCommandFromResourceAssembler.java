package com.levelupjourney.microservicechallenges.solutions.interfaces.rest.transform;

import com.levelupjourney.microservicechallenges.solutions.domain.model.commands.CreateSolutionCommand;
import com.levelupjourney.microservicechallenges.solutions.domain.model.valueobjects.ChallengeId;
import com.levelupjourney.microservicechallenges.solutions.domain.model.valueobjects.CodeVersionId;
import com.levelupjourney.microservicechallenges.solutions.domain.model.valueobjects.StudentId;
import com.levelupjourney.microservicechallenges.solutions.interfaces.rest.resource.CreateSolutionResource;

import java.util.UUID;

public class CreateSolutionCommandFromResourceAssembler {

    public static CreateSolutionCommand toCommandFromResource(CreateSolutionResource resource) {
        return new CreateSolutionCommand(
            new ChallengeId(UUID.fromString(resource.challengeId())),
            new CodeVersionId(UUID.fromString(resource.codeVersionId())),
            new StudentId(UUID.fromString(resource.studentId())),
            resource.code()
        );
    }
}