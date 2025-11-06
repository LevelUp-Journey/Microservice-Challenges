package com.levelupjourney.microservicechallenges.challenges.interfaces.rest.resource;

public record AddCodeVersionResource(String challengeId, String language, String defaultCode, String functionName) {
}