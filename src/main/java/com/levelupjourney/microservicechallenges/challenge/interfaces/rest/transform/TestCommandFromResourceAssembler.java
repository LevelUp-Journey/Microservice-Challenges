package com.levelupjourney.microservicechallenges.challenge.interfaces.rest.transform;

import com.levelupjourney.microservicechallenges.challenge.domain.model.commands.AddTestToChallengeCommand;
import com.levelupjourney.microservicechallenges.challenge.domain.model.commands.RemoveTestFromChallengeCommand;
import com.levelupjourney.microservicechallenges.challenge.domain.model.commands.UpdateTestCommand;
import com.levelupjourney.microservicechallenges.shared.domain.model.valueobjects.TestId;
import com.levelupjourney.microservicechallenges.challenge.interfaces.rest.resources.CreateTestResource;
import com.levelupjourney.microservicechallenges.challenge.interfaces.rest.resources.UpdateTestResource;

import java.util.UUID;

/**
 * Assembler class to convert REST resources to Test domain commands
 */
public class TestCommandFromResourceAssembler {

    public static AddTestToChallengeCommand toCommandFromResource(Long challengeVersionId, CreateTestResource resource) {
        return new AddTestToChallengeCommand(
                challengeVersionId,
                resource.title(),
                resource.hint(),
                resource.onErrorHint(),
                resource.testCode(),
                resource.input(),
                resource.expectedOutput()
        );
    }

    public static UpdateTestCommand toCommandFromResource(String testId, UpdateTestResource resource) {
        return new UpdateTestCommand(
                new TestId(UUID.fromString(testId)),
                resource.title(),
                resource.hint(),
                resource.onErrorHint(),
                resource.testCode(),
                resource.input(),
                resource.expectedOutput()
        );
    }

    public static RemoveTestFromChallengeCommand toDeleteCommandFromResource(String testId) {
        return new RemoveTestFromChallengeCommand(new TestId(UUID.fromString(testId)));
    }
}
