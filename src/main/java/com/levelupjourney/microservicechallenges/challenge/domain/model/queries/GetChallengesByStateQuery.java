package com.levelupjourney.microservicechallenges.challenge.domain.model.queries;

import com.levelupjourney.microservicechallenges.challenge.domain.model.valueobjects.ChallengeState;

public record GetChallengesByStateQuery(ChallengeState state) {
}
