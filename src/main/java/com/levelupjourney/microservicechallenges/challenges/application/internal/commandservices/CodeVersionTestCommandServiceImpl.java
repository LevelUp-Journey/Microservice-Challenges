package com.levelupjourney.microservicechallenges.challenges.application.internal.commandservices;

import com.levelupjourney.microservicechallenges.challenges.domain.model.commands.AddCodeVersionTestCommand;
import com.levelupjourney.microservicechallenges.challenges.domain.model.commands.UpdateCodeVersionTestCommand;
import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.CodeVersionTestId;
import com.levelupjourney.microservicechallenges.challenges.domain.services.CodeVersionTestCommandService;
import org.springframework.stereotype.Service;

@Service
public class CodeVersionTestCommandServiceImpl implements CodeVersionTestCommandService {
    @Override
    public CodeVersionTestId handle(AddCodeVersionTestCommand command) {
        return null;
    }

    @Override
    public void handle(UpdateCodeVersionTestCommand command) {

    }
}
