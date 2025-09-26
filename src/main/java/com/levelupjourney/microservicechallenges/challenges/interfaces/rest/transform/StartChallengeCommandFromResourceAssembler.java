package com.levelupjourney.microservicechallenges.challenges.interfaces.rest.transform;

import com.levelupjourney.microservicechallenges.challenges.domain.model.commands.StartChallengeCommand;
import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.ChallengeId;
import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.StudentId;
import com.levelupjourney.microservicechallenges.challenges.interfaces.rest.resource.StartChallengeResource;

import java.util.UUID;

public class StartChallengeCommandFromResourceAssembler {

    public static StartChallengeCommand toCommandFromResource(StartChallengeResource resource) {
        return new StartChallengeCommand(
            new ChallengeId(UUID.fromString(resource.challengeId())),
            new StudentId(UUID.fromString(resource.studentId()))
        );
    }
}