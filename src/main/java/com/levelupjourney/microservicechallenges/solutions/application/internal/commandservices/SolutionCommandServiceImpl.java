package com.levelupjourney.microservicechallenges.solutions.application.internal.commandservices;

import com.levelupjourney.microservicechallenges.solutions.domain.model.aggregates.Solution;
import com.levelupjourney.microservicechallenges.solutions.domain.model.commands.CreateSolutionCommand;
import com.levelupjourney.microservicechallenges.solutions.domain.model.commands.SubmitSolutionCommand;
import com.levelupjourney.microservicechallenges.solutions.domain.model.commands.UpdateSolutionCommand;
import com.levelupjourney.microservicechallenges.solutions.domain.model.valueobjects.SolutionReportId;
import com.levelupjourney.microservicechallenges.solutions.domain.services.SolutionCommandService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SolutionCommandServiceImpl implements SolutionCommandService {
    @Override
    public Optional<Solution> handle(CreateSolutionCommand command) {
        return Optional.empty();
    }

    @Override
    public SolutionReportId handle(SubmitSolutionCommand command) {
        return null;
    }

    @Override
    public void handle(UpdateSolutionCommand command) {

    }

}
