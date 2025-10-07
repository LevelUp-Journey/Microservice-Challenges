package com.levelupjourney.microservicechallenges.challenges.interfaces.rest.resource;

import java.util.List;
import java.util.Optional;

public record UpdateChallengeResource(Optional<String> name, Optional<String> description, Optional<Integer> experiencePoints, Optional<String> difficulty, Optional<String> status, List<String> tags) {
}
