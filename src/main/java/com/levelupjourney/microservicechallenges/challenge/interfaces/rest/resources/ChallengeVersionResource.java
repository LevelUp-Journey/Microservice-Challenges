package com.levelupjourney.microservicechallenges.challenge.interfaces.rest.resources;

import java.util.List;

public record ChallengeVersionResource(
        Long id,
        String language,
        String defaultStudentCode,
        List<TestResource> tests
) {
}
