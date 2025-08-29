package com.levelupjourney.microservicechallenges.challenge.interfaces.rest.transform;

import com.levelupjourney.microservicechallenges.challenge.domain.model.commands.*;
import com.levelupjourney.microservicechallenges.challenge.interfaces.rest.resources.*;
import com.levelupjourney.microservicechallenges.challenge.domain.model.valueobjects.TeacherId;
import com.levelupjourney.microservicechallenges.shared.domain.model.valueobjects.ChallengeId;
import com.levelupjourney.microservicechallenges.shared.domain.model.valueobjects.Language;
import com.levelupjourney.microservicechallenges.shared.domain.model.valueobjects.StudentId;
import com.levelupjourney.microservicechallenges.shared.domain.model.valueobjects.TestId;

import java.util.UUID;

/**
 * Assembler to transform REST resources into domain commands
 */
public class ChallengeCommandFromResourceAssembler {

    public static CreateChallengeCommand toCommandFromResource(CreateChallengeResource resource) {
        return new CreateChallengeCommand(
                new TeacherId(UUID.fromString(resource.teacherId())),
                resource.title(),
                resource.description()
        );
    }

    public static UpdateChallengeCommand toCommandFromResource(String challengeId, String teacherId, UpdateChallengeResource resource) {
        return new UpdateChallengeCommand(
                new ChallengeId(UUID.fromString(challengeId)),
                resource.title(),
                resource.description(),
                null, // difficulty - not implemented yet
                null, // tags - not implemented yet
                new TeacherId(UUID.fromString(teacherId))
        );
    }

    public static PublishChallengeCommand toCommandFromResource(String challengeId) {
        return new PublishChallengeCommand(
                new ChallengeId(UUID.fromString(challengeId))
        );
    }

    public static CreateChallengeVersionCommand toCommandFromResource(String challengeId, CreateChallengeVersionResource resource) {
        return new CreateChallengeVersionCommand(
                new ChallengeId(UUID.fromString(challengeId)),
                Language.valueOf(resource.language().toUpperCase()),
                resource.defaultStudentCode()
        );
    }

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

    public static StarChallengeByStudentIdCommand toStarCommandFromResource(String challengeId, String studentId) {
        return new StarChallengeByStudentIdCommand(
                new ChallengeId(UUID.fromString(challengeId)),
                new StudentId(UUID.fromString(studentId))
        );
    }

    public static UnStarChallengeByStudentIdCommand toUnstarCommandFromResource(String challengeId, String studentId) {
        return new UnStarChallengeByStudentIdCommand(
                new ChallengeId(UUID.fromString(challengeId)),
                new StudentId(UUID.fromString(studentId))
        );
    }

    public static RemoveTestFromChallengeCommand toRemoveTestCommandFromResource(String testId) {
        return new RemoveTestFromChallengeCommand(
                new TestId(UUID.fromString(testId))
        );
    }
}
