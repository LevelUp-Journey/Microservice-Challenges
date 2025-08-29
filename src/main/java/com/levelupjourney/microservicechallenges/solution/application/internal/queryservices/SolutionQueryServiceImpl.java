package com.levelupjourney.microservicechallenges.solution.application.internal.queryservices;

import com.levelupjourney.microservicechallenges.solution.domain.model.aggregates.Solution;
import com.levelupjourney.microservicechallenges.solution.domain.model.queries.GetAllSolutionsQuery;
import com.levelupjourney.microservicechallenges.solution.domain.model.queries.GetSolutionByIdQuery;
import com.levelupjourney.microservicechallenges.solution.domain.model.queries.GetSolutionByStudentIdAndChallengeIdQuery;
import com.levelupjourney.microservicechallenges.solution.domain.model.queries.GetSolutionsByChallengeIdQuery;
import com.levelupjourney.microservicechallenges.solution.domain.model.queries.GetSolutionsByStudentIdQuery;
import com.levelupjourney.microservicechallenges.solution.domain.services.SolutionQueryService;
import com.levelupjourney.microservicechallenges.solution.infrastructure.persistence.jpa.repository.SolutionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class SolutionQueryServiceImpl implements SolutionQueryService {

    private final SolutionRepository solutionRepository;

    public SolutionQueryServiceImpl(SolutionRepository solutionRepository) {
        this.solutionRepository = solutionRepository;
    }

    @Override
    public List<Solution> handle(GetAllSolutionsQuery query) {
        // TODO: Implement logic to get all solutions
        // 1. Retrieve all solutions from repository
        // 2. Apply any filtering if needed
        // 3. Return list of solutions
        return solutionRepository.findAll();
    }

    @Override
    public Optional<Solution> handle(GetSolutionByIdQuery query) {
        // TODO: Implement logic to get solution by ID
        // 1. Extract solution ID from query
        // 2. Find solution in repository
        // 3. Return optional solution
        return Optional.empty();
    }

    @Override
    public Optional<Solution> handle(GetSolutionByStudentIdAndChallengeIdQuery query) {
        // TODO: Implement logic to get solution by student and challenge
        // 1. Extract student ID and challenge ID from query
        // 2. Find solution for specific student and challenge
        // 3. Return optional solution
        return Optional.empty();
    }

    @Override
    public List<Solution> handle(GetSolutionsByChallengeIdQuery query) {
        // TODO: Implement logic to get solutions by challenge ID
        // 1. Extract challenge ID from query
        // 2. Find all solutions for the challenge
        // 3. Return list of solutions
        return List.of();
    }

    @Override
    public List<Solution> handle(GetSolutionsByStudentIdQuery query) {
        // TODO: Implement logic to get solutions by student ID
        // 1. Extract student ID from query
        // 2. Find all solutions by the student
        // 3. Return list of solutions
        return List.of();
    }
}
