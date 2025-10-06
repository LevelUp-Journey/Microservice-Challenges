package com.levelupjourney.microservicechallenges.solutions.interfaces.rest.transform;

import com.levelupjourney.microservicechallenges.solutions.domain.model.queries.GetSolutionByStudentIdAndCodeVersionIdQuery;
import com.levelupjourney.microservicechallenges.solutions.domain.model.valueobjects.CodeVersionId;
import com.levelupjourney.microservicechallenges.solutions.domain.model.valueobjects.StudentId;

import java.util.UUID;

public class GetSolutionByStudentIdAndCodeVersionIdQueryFromResourceAssembler {

    public static GetSolutionByStudentIdAndCodeVersionIdQuery toQueryFromResource(String studentId, String codeVersionId) {
        return new GetSolutionByStudentIdAndCodeVersionIdQuery(
            new StudentId(UUID.fromString(studentId)),
            new CodeVersionId(UUID.fromString(codeVersionId))
        );
    }
}