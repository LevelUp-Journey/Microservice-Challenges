package com.levelupjourney.microservicechallenges.solutionsreports.interfaces.rest.transform;

import com.levelupjourney.microservicechallenges.solutionsreports.domain.model.queries.GetReportsByStudentIdQuery;
import com.levelupjourney.microservicechallenges.solutionsreports.domain.model.valueobjects.StudentId;

import java.util.UUID;

public class GetReportsByStudentIdQueryFromResourceAssembler {
    
    public static GetReportsByStudentIdQuery toQueryFromStudentId(String studentId) {
        return new GetReportsByStudentIdQuery(new StudentId(UUID.fromString(studentId)));
    }
}