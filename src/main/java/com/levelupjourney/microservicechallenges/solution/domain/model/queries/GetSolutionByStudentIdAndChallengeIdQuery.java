package com.levelupjourney.microservicechallenges.solution.domain.model.queries;

import com.levelupjourney.microservicechallenges.shared.domain.model.valueobjects.ChallengeId;
import com.levelupjourney.microservicechallenges.shared.domain.model.valueobjects.StudentId;

public record GetSolutionByStudentIdAndChallengeIdQuery(
    StudentId studentId,
    ChallengeId challengeId
) {
}
