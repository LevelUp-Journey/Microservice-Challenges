package com.levelupjourney.microservicechallenges.solutionsreports.interfaces.rest.transform;

import com.levelupjourney.microservicechallenges.solutionsreports.domain.model.commands.CreateSolutionReportCommand;
import com.levelupjourney.microservicechallenges.solutionsreports.domain.model.valueobjects.CodeVersionTestId;
import com.levelupjourney.microservicechallenges.solutionsreports.domain.model.valueobjects.SolutionId;
import com.levelupjourney.microservicechallenges.solutionsreports.domain.model.valueobjects.StudentId;
import com.levelupjourney.microservicechallenges.solutionsreports.interfaces.rest.resources.CreateSolutionReportResource;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class CreateSolutionReportCommandFromResourceAssembler {
    
    public static CreateSolutionReportCommand toCommandFromResource(CreateSolutionReportResource resource) {
        return new CreateSolutionReportCommand(
                new SolutionId(UUID.fromString(resource.solutionId())),
                new StudentId(UUID.fromString(resource.studentId())),
                convertToCodeVersionTestIds(resource.successfulTestIds()),
                resource.timeTaken(),
                resource.memoryUsed()
        );
    }
    
    private static List<CodeVersionTestId> convertToCodeVersionTestIds(List<String> testIds) {
        if (testIds == null) {
            return null;
        }
        return testIds.stream()
                .map(id -> new CodeVersionTestId(UUID.fromString(id)))
                .collect(Collectors.toList());
    }
}