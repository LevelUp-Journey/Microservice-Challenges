package com.levelupjourney.microservicechallenges.solutions.domain.services;

import com.levelupjourney.microservicechallenges.solutions.domain.model.aggregates.Solution;
import com.levelupjourney.microservicechallenges.solutions.domain.model.commands.CreateSolutionCommand;
import com.levelupjourney.microservicechallenges.solutions.domain.model.commands.SubmitSolutionCommand;
import com.levelupjourney.microservicechallenges.solutions.domain.model.commands.UpdateSolutionCommand;
import com.levelupjourney.microservicechallenges.solutions.domain.model.valueobjects.SolutionReportId;

import java.util.Optional;

public interface SolutionCommandService {
    Optional<Solution> handle(CreateSolutionCommand command);
    Optional<SolutionReportId> handle(SubmitSolutionCommand command);
    void handle(UpdateSolutionCommand command);
}
