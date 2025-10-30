package com.levelupjourney.microservicechallenges.challenges.interfaces.rest.resource;

import java.util.List;

public record CodeVersionResource(
    String id,
    String challengeId,
    String language,
    String initialCode,
    String functionName,
    List<CodeVersionTestResource> tests
) {
}