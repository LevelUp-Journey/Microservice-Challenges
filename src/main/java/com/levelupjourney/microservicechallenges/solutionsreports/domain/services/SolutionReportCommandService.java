package com.levelupjourney.microservicechallenges.solutionsreports.domain.services;

import com.levelupjourney.microservicechallenges.solutionsreports.domain.model.aggregates.SolutionReport;
import com.levelupjourney.microservicechallenges.solutionsreports.domain.model.commands.CreateSolutionReportCommand;
import com.levelupjourney.microservicechallenges.solutionsreports.domain.model.commands.DeleteSolutionReportBySolutionIdCommand;
import com.levelupjourney.microservicechallenges.solutionsreports.domain.model.commands.DeleteSolutionReportCommand;
import com.levelupjourney.microservicechallenges.solutionsreports.domain.model.commands.DeleteSolutionReportsByStudentIdCommand;

import java.util.Optional;

public interface SolutionReportCommandService {
    Optional<SolutionReport> handle(CreateSolutionReportCommand command);
    boolean handle(DeleteSolutionReportBySolutionIdCommand command);
    boolean handle(DeleteSolutionReportCommand command);
    int handle(DeleteSolutionReportsByStudentIdCommand command);
}
