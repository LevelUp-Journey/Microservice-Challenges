package com.levelupjourney.microservicechallenges.challenges.domain.services;

import com.levelupjourney.microservicechallenges.challenges.domain.model.commands.CreateTagCommand;
import com.levelupjourney.microservicechallenges.challenges.domain.model.commands.UpdateTagCommand;
import com.levelupjourney.microservicechallenges.challenges.domain.model.commands.DeleteTagCommand;
import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.TagId;

public interface TagCommandService {
    TagId handle(CreateTagCommand command);
    void handle(UpdateTagCommand command);
    void handle(DeleteTagCommand command);
}