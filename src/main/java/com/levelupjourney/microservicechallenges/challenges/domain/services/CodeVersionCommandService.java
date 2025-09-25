package com.levelupjourney.microservicechallenges.challenges.domain.services;

import com.levelupjourney.microservicechallenges.challenges.domain.model.commands.AddCodeVersionCommand;
import com.levelupjourney.microservicechallenges.challenges.domain.model.commands.UpdateCodeVersionCommand;
import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.CodeVersionId;

public interface CodeVersionCommandService {

    CodeVersionId handle(AddCodeVersionCommand command);
    void handle(UpdateCodeVersionCommand command);

}
