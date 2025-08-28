package com.levelupjourney.microservicechallenges.solution.interfaces.rest.transform;

import com.levelupjourney.microservicechallenges.solution.domain.model.queries.*;
import com.levelupjourney.microservicechallenges.solution.domain.model.valueobjects.SolutionId;
import com.levelupjourney.microservicechallenges.shared.domain.model.valueobjects.ChallengeId;
import com.levelupjourney.microservicechallenges.shared.domain.model.valueobjects.StudentId;

import java.util.UUID;

/**
 * Assembler to create query objects from request parameters
 */
public class SolutionQueryFromParametersAssembler {

    public static GetAllSolutionsQuery toGetAllSolutionsQuery() {
        return new GetAllSolutionsQuery();
    }

    public static GetSolutionByIdQuery toGetSolutionByIdQuery(String solutionId) {
        return new GetSolutionByIdQuery(
                new SolutionId(UUID.fromString(solutionId))
        );
    }

    public static GetSolutionByStudentIdAndChallengeIdQuery toGetSolutionByStudentIdAndChallengeIdQuery(String studentId, String challengeId) {
        return new GetSolutionByStudentIdAndChallengeIdQuery(
                new StudentId(UUID.fromString(studentId)),
                new ChallengeId(UUID.fromString(challengeId))
        );
    }

    public static GetSolutionsByChallengeIdQuery toGetSolutionsByChallengeIdQuery(String challengeId) {
        return new GetSolutionsByChallengeIdQuery(
                new ChallengeId(UUID.fromString(challengeId))
        );
    }

    public static GetSolutionsByStudentIdQuery toGetSolutionsByStudentIdQuery(String studentId) {
        return new GetSolutionsByStudentIdQuery(
                new StudentId(UUID.fromString(studentId))
        );
    }
}
