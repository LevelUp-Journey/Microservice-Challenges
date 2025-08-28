package com.levelupjourney.microservicechallenges.challenge.domain.services;

import com.levelupjourney.microservicechallenges.challenge.domain.model.aggregates.Test;
import com.levelupjourney.microservicechallenges.challenge.domain.model.queries.GetChallengeTestsByChallengeIdQuery;
import com.levelupjourney.microservicechallenges.challenge.domain.model.queries.GetTestByIdQuery;

import java.util.List;
import java.util.Optional;

public interface TestQueryService {
    public List<Test> handle(GetChallengeTestsByChallengeIdQuery query);
    public Optional<Test> handle(GetTestByIdQuery query);
}
