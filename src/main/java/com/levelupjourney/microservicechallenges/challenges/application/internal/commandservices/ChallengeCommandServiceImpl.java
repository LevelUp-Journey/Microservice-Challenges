package com.levelupjourney.microservicechallenges.challenges.application.internal.commandservices;

import com.levelupjourney.microservicechallenges.challenges.domain.model.commands.CreateChallengeCommand;
import com.levelupjourney.microservicechallenges.challenges.domain.model.commands.PublishChallengeCommand;
import com.levelupjourney.microservicechallenges.challenges.domain.model.commands.StartChallengeCommand;
import com.levelupjourney.microservicechallenges.challenges.domain.model.commands.UpdateChallengeCommand;
import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.ChallengeId;
import com.levelupjourney.microservicechallenges.challenges.domain.services.ChallengeCommandService;
import org.springframework.stereotype.Service;


@Service
public class ChallengeCommandServiceImpl implements ChallengeCommandService {
    @Override
    public ChallengeId handle(CreateChallengeCommand command) {
        return null;
    }

    @Override
    public ChallengeId handle(PublishChallengeCommand command) {
        return null;
    }

    @Override
    public void handle(StartChallengeCommand command) {

    }

    @Override
    public void handle(UpdateChallengeCommand command) {

    }
    
}
