package com.levelupjourney.microservicechallenges.solutions.domain.model.queries;

import com.levelupjourney.microservicechallenges.solutions.domain.model.valueobjects.ChallengeId;
import com.levelupjourney.microservicechallenges.solutions.domain.model.valueobjects.CodeVersionId;
import com.levelupjourney.microservicechallenges.solutions.domain.model.valueobjects.StudentId;

public record GetSolutionByChallengeIdAndCodeVersionIdAndStudentIdQuery(
        ChallengeId challengeId,
        CodeVersionId codeVersionId,
        StudentId studentId
) {
}