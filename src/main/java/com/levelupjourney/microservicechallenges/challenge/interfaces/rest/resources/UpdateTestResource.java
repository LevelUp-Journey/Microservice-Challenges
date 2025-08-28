package com.levelupjourney.microservicechallenges.challenge.interfaces.rest.resources;

public record UpdateTestResource(
        String title,
        String hint,
        String onErrorHint,
        String testCode,
        String input,
        String expectedOutput
) {
}
