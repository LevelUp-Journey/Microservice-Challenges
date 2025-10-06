package com.levelupjourney.microservicechallenges.solutionsreports.interfaces.rest.transform;

import com.levelupjourney.microservicechallenges.solutionsreports.domain.model.commands.DeleteSolutionReportBySolutionIdCommand;
import com.levelupjourney.microservicechallenges.solutionsreports.domain.model.valueobjects.SolutionId;

import java.util.UUID;

public class DeleteSolutionReportBySolutionIdCommandFromResourceAssembler {
    
    public static DeleteSolutionReportBySolutionIdCommand toCommandFromSolutionId(String solutionId) {
        return new DeleteSolutionReportBySolutionIdCommand(new SolutionId(UUID.fromString(solutionId)));
    }
}