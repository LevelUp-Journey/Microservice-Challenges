package com.levelupjourney.microservicechallenges.challenges.domain.model.commands;

import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.TagId;

public record UpdateTagCommand(
        TagId tagId,
        String name,
        String color,
        String iconUrl
) {
}