package com.levelupjourney.microservicechallenges.challenges.domain.services;

import com.levelupjourney.microservicechallenges.challenges.domain.model.commands.AddCodeVersionTestCommand;
import com.levelupjourney.microservicechallenges.challenges.domain.model.commands.UpdateCodeVersionTestCommand;
import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.CodeVersionTestId;

public interface CodeVersionTestCommandService {

    CodeVersionTestId handle(AddCodeVersionTestCommand command);
    void handle(UpdateCodeVersionTestCommand command);
    
}
