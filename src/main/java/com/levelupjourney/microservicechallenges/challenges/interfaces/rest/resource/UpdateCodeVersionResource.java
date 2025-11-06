package com.levelupjourney.microservicechallenges.challenges.interfaces.rest.resource;

import java.util.Optional;

public record UpdateCodeVersionResource(Optional<String> code, Optional<String> functionName) {
}