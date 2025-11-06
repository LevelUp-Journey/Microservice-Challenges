package com.levelupjourney.microservicechallenges.challenges.interfaces.rest.resource;

import java.util.List;

public record BatchCodeVersionsRequest(
    List<String> challengeIds
) {
}
