package com.levelupjourney.microservicechallenges.solution.domain.services;

import com.levelupjourney.microservicechallenges.solution.domain.model.aggregates.Solution;
import com.levelupjourney.microservicechallenges.solution.domain.model.commands.CreateSolutionCommand;
import java.util.Optional;

public interface SolutionCommandService {
    public Optional<Solution> handle(CreateSolutionCommand command);
}
