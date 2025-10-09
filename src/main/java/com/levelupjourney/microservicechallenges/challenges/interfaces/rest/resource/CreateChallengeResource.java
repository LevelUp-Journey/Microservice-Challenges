package com.levelupjourney.microservicechallenges.challenges.interfaces.rest.resource;

public record CreateChallengeResource(String teacherId, String name, String description, Integer experiencePoints, String difficulty) {


}
