package com.levelupjourney.microservicechallenges.challenge.interfaces.rest.transform;

import com.levelupjourney.microservicechallenges.challenge.domain.model.queries.*;
import com.levelupjourney.microservicechallenges.challenge.domain.model.valueobjects.ChallengeState;
import com.levelupjourney.microservicechallenges.challenge.domain.model.valueobjects.TeacherId;
import com.levelupjourney.microservicechallenges.shared.domain.model.valueobjects.ChallengeId;
import com.levelupjourney.microservicechallenges.shared.domain.model.valueobjects.StudentId;
import com.levelupjourney.microservicechallenges.shared.domain.model.valueobjects.TestId;

import java.util.UUID;

/**
 * Assembler to create query objects from request parameters
 */
public class ChallengeQueryFromParametersAssembler {

    public static GetAllChallengesQuery toGetAllChallengesQuery() {
        return new GetAllChallengesQuery();
    }

    public static GetChallengeByIdQuery toGetChallengeByIdQuery(String challengeId) {
        return new GetChallengeByIdQuery(
                new ChallengeId(UUID.fromString(challengeId))
        );
    }

    public static GetChallengesByStateQuery toGetChallengesByStateQuery(String state) {
        return new GetChallengesByStateQuery(
                ChallengeState.valueOf(state.toUpperCase())
        );
    }

    public static GetChallengesByTeacherIdQuery toGetChallengesByTeacherIdQuery(String teacherId) {
        return new GetChallengesByTeacherIdQuery(
                new TeacherId(UUID.fromString(teacherId))
        );
    }

    public static GetChallengeStarsAmountQuery toGetChallengeStarsAmountQuery(String challengeId) {
        return new GetChallengeStarsAmountQuery(
                new ChallengeId(UUID.fromString(challengeId))
        );
    }

    public static GetChallengeTestsByChallengeIdQuery toGetChallengeTestsByChallengeIdQuery(String challengeId) {
        return new GetChallengeTestsByChallengeIdQuery(
                new ChallengeId(UUID.fromString(challengeId))
        );
    }

    public static GetStarredChallengesByStudentIdQuery toGetStarredChallengesByStudentIdQuery(String studentId) {
        return new GetStarredChallengesByStudentIdQuery(
                new StudentId(UUID.fromString(studentId))
        );
    }

    public static GetTestByIdQuery toGetTestByIdQuery(String testId) {
        return new GetTestByIdQuery(
                new TestId(UUID.fromString(testId))
        );
    }
}
