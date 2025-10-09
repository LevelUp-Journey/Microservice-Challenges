package com.levelupjourney.microservicechallenges.solutions.interfaces.rest.resource;

public record CreateSolutionResource(String challengeId, String codeVersionId, String studentId, String code) {
}