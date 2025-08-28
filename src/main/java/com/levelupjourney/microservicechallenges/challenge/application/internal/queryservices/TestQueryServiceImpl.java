package com.levelupjourney.microservicechallenges.challenge.application.internal.queryservices;

import com.levelupjourney.microservicechallenges.challenge.domain.model.aggregates.Test;
import com.levelupjourney.microservicechallenges.challenge.domain.model.queries.GetChallengeTestsByChallengeIdQuery;
import com.levelupjourney.microservicechallenges.challenge.domain.model.queries.GetTestByIdQuery;
import com.levelupjourney.microservicechallenges.challenge.domain.services.TestQueryService;
import com.levelupjourney.microservicechallenges.challenge.infrastructure.persistence.jpa.repository.TestRepository;
import com.levelupjourney.microservicechallenges.challenge.infrastructure.persistence.jpa.repository.ChallengeVersionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class TestQueryServiceImpl implements TestQueryService {

    private final TestRepository testRepository;
    private final ChallengeVersionRepository challengeVersionRepository;

    public TestQueryServiceImpl(TestRepository testRepository, 
                              ChallengeVersionRepository challengeVersionRepository) {
        this.testRepository = testRepository;
        this.challengeVersionRepository = challengeVersionRepository;
    }

    @Override
    public List<Test> handle(GetChallengeTestsByChallengeIdQuery query) {
        // TODO: Implement logic to get tests for a challenge
        // 1. Extract challenge ID from query
        // 2. Find all challenge versions for the challenge
        // 3. Find all tests for those versions
        // 4. Return list of tests
        return List.of();
    }

    @Override
    public Optional<Test> handle(GetTestByIdQuery query) {
        // TODO: Implement logic to get test by ID
        // 1. Extract test ID from query
        // 2. Find test in repository
        // 3. Return optional test
        return Optional.empty();
    }
}
