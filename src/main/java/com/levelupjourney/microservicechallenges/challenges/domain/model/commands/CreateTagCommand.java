package com.levelupjourney.microservicechallenges.challenges.domain.model.commands;

public record CreateTagCommand(
        String name,
        String color,
        String iconUrl
) {
}