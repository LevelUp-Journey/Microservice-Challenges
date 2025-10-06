package com.levelupjourney.microservicechallenges.solutions.domain.model.queries;

import com.levelupjourney.microservicechallenges.solutions.domain.model.valueobjects.CodeVersionId;
import com.levelupjourney.microservicechallenges.solutions.domain.model.valueobjects.StudentId;

public record GetSolutionByStudentIdAndCodeVersionIdQuery(StudentId studentId, CodeVersionId codeVersionId) {
}
