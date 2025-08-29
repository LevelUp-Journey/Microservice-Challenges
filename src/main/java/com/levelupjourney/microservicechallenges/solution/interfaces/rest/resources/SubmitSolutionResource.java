package com.levelupjourney.microservicechallenges.solution.interfaces.rest.resources;

public record SubmitSolutionResource(
        String studentId,
        String code,
        String language
) {
}
