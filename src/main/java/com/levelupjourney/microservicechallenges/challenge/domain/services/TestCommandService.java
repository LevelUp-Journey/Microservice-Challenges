package com.levelupjourney.microservicechallenges.challenge.domain.services;

import com.levelupjourney.microservicechallenges.challenge.domain.model.commands.PublishChallengeCommand;
import com.levelupjourney.microservicechallenges.challenge.domain.model.commands.RemoveTestFromChallengeCommand;
import com.levelupjourney.microservicechallenges.challenge.domain.model.commands.StarChallengeByStudentIdCommand;
import com.levelupjourney.microservicechallenges.challenge.domain.model.commands.UnStarChallengeByStudentIdCommand;

public interface TestCommandService {
    public void handle(PublishChallengeCommand command);
    public void handle(RemoveTestFromChallengeCommand command);
    public void handle(StarChallengeByStudentIdCommand command);
    public void handle(UnStarChallengeByStudentIdCommand command);
}
