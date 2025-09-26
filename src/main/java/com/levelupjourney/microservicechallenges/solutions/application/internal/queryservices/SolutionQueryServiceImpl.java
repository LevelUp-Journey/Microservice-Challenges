package com.levelupjourney.microservicechallenges.solutions.application.internal.queryservices;

import com.levelupjourney.microservicechallenges.solutions.domain.model.aggregates.Solution;
import com.levelupjourney.microservicechallenges.solutions.domain.model.queries.GetSolutionByIdQuery;
import com.levelupjourney.microservicechallenges.solutions.domain.model.queries.GetSolutionByStudentIdAndCodeVersionIdQuery;
import com.levelupjourney.microservicechallenges.solutions.domain.services.SolutionQueryService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SolutionQueryServiceImpl implements SolutionQueryService {
    @Override
    public Optional<Solution> handle(GetSolutionByIdQuery query) {
        return Optional.empty();
    }

    @Override
    public Optional<Solution> handle(GetSolutionByStudentIdAndCodeVersionIdQuery query) {
        return Optional.empty();
    }

}
