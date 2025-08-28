package com.levelupjourney.microservicechallenges.challenge.application.internal.queryservices;

import com.levelupjourney.microservicechallenges.challenge.domain.model.aggregates.Challenge;
import com.levelupjourney.microservicechallenges.challenge.domain.model.aggregates.Test;
import com.levelupjourney.microservicechallenges.challenge.domain.model.queries.GetAllChallengesQuery;
import com.levelupjourney.microservicechallenges.challenge.domain.model.queries.GetChallengeByIdQuery;
import com.levelupjourney.microservicechallenges.challenge.domain.model.queries.GetChallengesByStateQuery;
import com.levelupjourney.microservicechallenges.challenge.domain.model.queries.GetChallengesByTeacherIdQuery;
import com.levelupjourney.microservicechallenges.challenge.domain.model.queries.GetChallengeStarsAmountQuery;
import com.levelupjourney.microservicechallenges.challenge.domain.model.queries.GetChallengeTestsByChallengeIdQuery;
import com.levelupjourney.microservicechallenges.challenge.domain.model.queries.GetStarredChallengesByStudentIdQuery;
import com.levelupjourney.microservicechallenges.challenge.domain.model.queries.GetTestByIdQuery;
import com.levelupjourney.microservicechallenges.challenge.domain.services.ChallengeQueryService;
import com.levelupjourney.microservicechallenges.challenge.infrastructure.persistence.jpa.repository.ChallengeRepository;
import com.levelupjourney.microservicechallenges.challenge.infrastructure.persistence.jpa.repository.TestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class ChallengeQueryServiceImpl implements ChallengeQueryService {

    private final ChallengeRepository challengeRepository;
    private final TestRepository testRepository;

    public ChallengeQueryServiceImpl(ChallengeRepository challengeRepository, 
                                   TestRepository testRepository) {
        this.challengeRepository = challengeRepository;
        this.testRepository = testRepository;
    }

    @Override
    public List<Challenge> handle(GetAllChallengesQuery query) {
        // TODO: Implement logic to get all challenges
        // 1. Retrieve all challenges from repository
        // 2. Apply any filtering if needed
        // 3. Return list of challenges
        return challengeRepository.findAll();
    }

    @Override
    public Optional<Challenge> handle(GetChallengeByIdQuery query) {
        // TODO: Implement logic to get challenge by ID
        // 1. Extract challenge ID from query
        // 2. Find challenge in repository
        // 3. Return optional challenge
        return Optional.empty();
    }

    @Override
    public List<Challenge> handle(GetChallengesByStateQuery query) {
        // TODO: Implement logic to get challenges by state
        // 1. Extract state from query
        // 2. Find challenges with matching state
        // 3. Return list of challenges
        return List.of();
    }

    @Override
    public List<Challenge> handle(GetChallengesByTeacherIdQuery query) {
        // TODO: Implement logic to get challenges by teacher ID
        // 1. Extract teacher ID from query
        // 2. Find challenges created by teacher
        // 3. Return list of challenges
        return List.of();
    }

    @Override
    public int handle(GetChallengeStarsAmountQuery query) {
        // TODO: Implement logic to get challenge stars count
        // 1. Extract challenge ID from query
        // 2. Find challenge
        // 3. Count stars in the challenge
        // 4. Return count
        return 0;
    }

    @Override
    public List<Test> handle(GetChallengeTestsByChallengeIdQuery query) {
        // TODO: Implement logic to get tests for a challenge
        // 1. Extract challenge ID from query
        // 2. Find all tests for the challenge versions
        // 3. Return list of tests
        return List.of();
    }

    @Override
    public List<Challenge> handle(GetStarredChallengesByStudentIdQuery query) {
        // TODO: Implement logic to get starred challenges by student
        // 1. Extract student ID from query
        // 2. Find challenges starred by the student
        // 3. Return list of challenges
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
