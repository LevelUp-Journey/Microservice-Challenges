package com.levelupjourney.microservicechallenges.challenges.domain.services;

import com.levelupjourney.microservicechallenges.challenges.domain.model.commands.AddGuideCommand;
import com.levelupjourney.microservicechallenges.challenges.domain.model.commands.CreateChallengeCommand;
import com.levelupjourney.microservicechallenges.challenges.domain.model.commands.DeleteChallengeCommand;
import com.levelupjourney.microservicechallenges.challenges.domain.model.commands.RemoveGuideCommand;
import com.levelupjourney.microservicechallenges.challenges.domain.model.commands.StartChallengeCommand;
import com.levelupjourney.microservicechallenges.challenges.domain.model.commands.StartChallengeResult;
import com.levelupjourney.microservicechallenges.challenges.domain.model.commands.UpdateChallengeCommand;
import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.ChallengeId;

public interface ChallengeCommandService {
    ChallengeId handle(CreateChallengeCommand command);
    StartChallengeResult handle(StartChallengeCommand command);
    void handle(UpdateChallengeCommand command);
    void handle(DeleteChallengeCommand command);
    void handle(AddGuideCommand command);
    void handle(RemoveGuideCommand command);
}
