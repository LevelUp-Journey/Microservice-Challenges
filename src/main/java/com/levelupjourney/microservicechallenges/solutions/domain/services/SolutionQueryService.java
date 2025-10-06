package com.levelupjourney.microservicechallenges.solutions.domain.services;

import com.levelupjourney.microservicechallenges.solutions.domain.model.aggregates.Solution;
import com.levelupjourney.microservicechallenges.solutions.domain.model.queries.GetSolutionByChallengeIdAndCodeVersionIdAndStudentIdQuery;
import com.levelupjourney.microservicechallenges.solutions.domain.model.queries.GetSolutionByIdQuery;
import com.levelupjourney.microservicechallenges.solutions.domain.model.queries.GetSolutionByStudentIdAndCodeVersionIdQuery;

import java.util.Optional;

public interface SolutionQueryService {
    Optional<Solution> handle(GetSolutionByIdQuery query);
    Optional<Solution> handle(GetSolutionByStudentIdAndCodeVersionIdQuery query);
    Optional<Solution> handle(GetSolutionByChallengeIdAndCodeVersionIdAndStudentIdQuery query);
}
