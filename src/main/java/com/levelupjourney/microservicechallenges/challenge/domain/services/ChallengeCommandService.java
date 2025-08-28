package com.levelupjourney.microservicechallenges.challenge.domain.services;

import com.levelupjourney.microservicechallenges.challenge.domain.model.aggregates.Challenge;
import com.levelupjourney.microservicechallenges.challenge.domain.model.aggregates.Test;
import com.levelupjourney.microservicechallenges.challenge.domain.model.commands.AddTestToChallengeCommand;
import com.levelupjourney.microservicechallenges.challenge.domain.model.commands.CreateChallengeCommand;
import com.levelupjourney.microservicechallenges.challenge.domain.model.commands.CreateChallengeVersionCommand;
import com.levelupjourney.microservicechallenges.challenge.domain.model.commands.PublishChallengeCommand;
import com.levelupjourney.microservicechallenges.challenge.domain.model.commands.RemoveTestFromChallengeCommand;
import com.levelupjourney.microservicechallenges.challenge.domain.model.commands.StarChallengeByStudentIdCommand;
import com.levelupjourney.microservicechallenges.challenge.domain.model.commands.UnStarChallengeByStudentIdCommand;
import com.levelupjourney.microservicechallenges.challenge.domain.model.commands.UpdateChallengeCommand;
import com.levelupjourney.microservicechallenges.challenge.domain.model.commands.UpdateTestCommand;
import com.levelupjourney.microservicechallenges.challenge.domain.model.entities.ChallengeVersion;
import java.util.Optional;

public interface ChallengeCommandService {
    public Optional<Test> handle(AddTestToChallengeCommand command);
    public Optional<Challenge> handle(CreateChallengeCommand command);
    public Optional<ChallengeVersion> handle(CreateChallengeVersionCommand command);
    public void handle(PublishChallengeCommand command);
    public void handle(RemoveTestFromChallengeCommand command);
    public void handle(StarChallengeByStudentIdCommand command);
    public void handle(UnStarChallengeByStudentIdCommand command);
    public Optional<Challenge> handle(UpdateChallengeCommand command);
    public Optional<Test> handle(UpdateTestCommand command);
}
