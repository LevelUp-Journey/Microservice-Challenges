package com.levelupjourney.microservicechallenges.solution.domain.services;

import com.levelupjourney.microservicechallenges.solution.domain.model.aggregates.Solution;
import com.levelupjourney.microservicechallenges.solution.domain.model.commands.CreateSolutionCommand;
import com.levelupjourney.microservicechallenges.solution.domain.model.commands.SubmitSolutionCommand;
import com.levelupjourney.microservicechallenges.solution.domain.model.commands.UpdateSolutionCommand;
import java.util.Optional;

public interface SolutionCommandService {
    public Optional<Solution> handle(CreateSolutionCommand command);
    public Optional<Solution> handle(SubmitSolutionCommand command);
    public Optional<Solution> handle(UpdateSolutionCommand command);
}
