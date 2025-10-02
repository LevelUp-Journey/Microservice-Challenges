package com.levelupjourney.microservicechallenges.challenges.domain.services;

import com.levelupjourney.microservicechallenges.challenges.domain.model.commands.CreateChallengeCommand;
import com.levelupjourney.microservicechallenges.challenges.domain.model.commands.StartChallengeCommand;
import com.levelupjourney.microservicechallenges.challenges.domain.model.commands.UpdateChallengeCommand;
import com.levelupjourney.microservicechallenges.challenges.domain.model.commands.AssignTagToChallengeCommand;
import com.levelupjourney.microservicechallenges.challenges.domain.model.commands.UnassignTagFromChallengeCommand;
import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.ChallengeId;

public interface ChallengeCommandService {
    ChallengeId handle(CreateChallengeCommand command);
    void handle(StartChallengeCommand command);
    void handle(UpdateChallengeCommand command);
    void handle(AssignTagToChallengeCommand command);
    void handle(UnassignTagFromChallengeCommand command);
}
