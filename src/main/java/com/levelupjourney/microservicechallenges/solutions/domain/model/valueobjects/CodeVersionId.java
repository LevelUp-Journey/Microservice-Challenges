package com.levelupjourney.microservicechallenges.solutions.domain.model.valueobjects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.util.UUID;

@Embeddable
public record CodeVersionId(@Column(name = "id") UUID id) {
}
