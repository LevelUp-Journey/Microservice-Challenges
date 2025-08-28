package com.levelupjourney.microservicechallenges.challenge.domain.model.queries;

import com.levelupjourney.microservicechallenges.shared.domain.model.valueobjects.TestId;

public record GetTestByIdQuery(TestId testId) {
}
