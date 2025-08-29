package com.levelupjourney.microservicechallenges.solution.interfaces.rest.transform;

import com.levelupjourney.microservicechallenges.solution.domain.model.commands.*;
import com.levelupjourney.microservicechallenges.solution.interfaces.rest.resources.*;
import com.levelupjourney.microservicechallenges.solution.domain.model.valueobjects.SolutionId;
import com.levelupjourney.microservicechallenges.shared.domain.model.valueobjects.ChallengeId;
import com.levelupjourney.microservicechallenges.shared.domain.model.valueobjects.Language;
import com.levelupjourney.microservicechallenges.shared.domain.model.valueobjects.StudentId;

import java.util.UUID;

/**
 * Assembler to transform REST resources into domain commands
 */
public class SolutionCommandFromResourceAssembler {

    public static CreateSolutionCommand toCommandFromResource(CreateSolutionResource resource) {
        return new CreateSolutionCommand(
                new StudentId(UUID.fromString(resource.studentId())),
                new ChallengeId(UUID.fromString(resource.challengeId())),
                Language.valueOf(resource.language().toUpperCase()),
                resource.code()
        );
    }

    public static SubmitSolutionCommand toCommandFromResource(String challengeId, SubmitSolutionResource resource) {
        return new SubmitSolutionCommand(
                new ChallengeId(UUID.fromString(challengeId)),
                new StudentId(UUID.fromString(resource.studentId())),
                resource.code(),
                Language.valueOf(resource.language().toUpperCase())
        );
    }

    public static SubmitSolutionCommand toSubmitCommandFromResource(String solutionId) {
        // Note: This is a simplified version. In a real implementation, you would
        // fetch the solution by ID first to get the challengeId and studentId
        throw new UnsupportedOperationException("Submit by solutionId requires solution lookup - implement in service layer");
    }

    public static UpdateSolutionCommand toCommandFromResource(String solutionId, UpdateSolutionResource resource) {
        return new UpdateSolutionCommand(
                new SolutionId(UUID.fromString(solutionId)),
                resource.code(),
                Language.valueOf(resource.language().toUpperCase())
        );
    }
}
