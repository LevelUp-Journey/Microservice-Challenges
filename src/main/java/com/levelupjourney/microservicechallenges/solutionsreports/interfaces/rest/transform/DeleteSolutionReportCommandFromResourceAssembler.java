package com.levelupjourney.microservicechallenges.solutionsreports.interfaces.rest.transform;

import com.levelupjourney.microservicechallenges.solutionsreports.domain.model.commands.DeleteSolutionReportCommand;
import com.levelupjourney.microservicechallenges.solutionsreports.domain.model.valueobjects.SolutionReportId;

import java.util.UUID;

public class DeleteSolutionReportCommandFromResourceAssembler {
    
    public static DeleteSolutionReportCommand toCommandFromReportId(String reportId) {
        return new DeleteSolutionReportCommand(new SolutionReportId(UUID.fromString(reportId)));
    }
}