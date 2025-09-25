package com.levelupjourney.microservicechallenges.challenges.application.internal.commandservices;

import com.levelupjourney.microservicechallenges.challenges.domain.model.commands.AddCodeVersionCommand;
import com.levelupjourney.microservicechallenges.challenges.domain.model.commands.UpdateCodeVersionCommand;
import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.CodeVersionId;
import com.levelupjourney.microservicechallenges.challenges.domain.services.CodeVersionCommandService;
import org.springframework.stereotype.Service;

@Service
public class CodeVersionCommandServiceImpl implements CodeVersionCommandService {

    @Override
    public CodeVersionId handle(AddCodeVersionCommand command) {
        return null;
    }

    @Override
    public void handle(UpdateCodeVersionCommand command) {

    }
}
