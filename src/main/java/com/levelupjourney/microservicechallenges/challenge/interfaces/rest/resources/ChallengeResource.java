package com.levelupjourney.microservicechallenges.challenge.interfaces.rest.resources;

import java.util.List;

public record ChallengeResource(
        String id,
        String teacherId,
        String title,
        String description,
        String state,
        List<StarResource> stars,
        List<ChallengeVersionResource> versions,
        int starsCount
) {
}
