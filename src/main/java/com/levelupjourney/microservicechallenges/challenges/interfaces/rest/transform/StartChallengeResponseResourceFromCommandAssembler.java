package com.levelupjourney.microservicechallenges.challenges.interfaces.rest.transform;

import com.levelupjourney.microservicechallenges.challenges.domain.model.commands.StartChallengeCommand;
import com.levelupjourney.microservicechallenges.challenges.domain.model.commands.StartChallengeResult;
import com.levelupjourney.microservicechallenges.challenges.interfaces.rest.resource.StartChallengeResponseResource;

public class StartChallengeResponseResourceFromCommandAssembler {

    public static StartChallengeResponseResource toResourceFromCommand(StartChallengeCommand command, StartChallengeResult result) {
        return new StartChallengeResponseResource(
                command.challengeId().id().toString(),
                command.codeVersionId().id().toString(),
                command.studentId().id().toString(),
                result.solutionId(),
                result.isNewSolution()
        );
    }
}