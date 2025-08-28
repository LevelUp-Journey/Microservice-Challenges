package com.levelupjourney.microservicechallenges.challenge.domain.services;

import com.levelupjourney.microservicechallenges.challenge.domain.model.aggregates.Test;
import com.levelupjourney.microservicechallenges.challenge.domain.model.commands.AddTestToChallengeCommand;
import com.levelupjourney.microservicechallenges.challenge.domain.model.commands.RemoveTestFromChallengeCommand;
import com.levelupjourney.microservicechallenges.challenge.domain.model.commands.UpdateTestCommand;
import java.util.Optional;

public interface TestCommandService {
    public Optional<Test> handle(AddTestToChallengeCommand command);
    public void handle(RemoveTestFromChallengeCommand command);
    public Optional<Test> handle(UpdateTestCommand command);
}
