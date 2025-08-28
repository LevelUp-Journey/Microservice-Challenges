package com.levelupjourney.microservicechallenges.solution.domain.services;

import com.levelupjourney.microservicechallenges.solution.domain.model.aggregates.Solution;
import com.levelupjourney.microservicechallenges.solution.domain.model.queries.GetAllSolutionsQuery;
import com.levelupjourney.microservicechallenges.solution.domain.model.queries.GetSolutionByIdQuery;
import com.levelupjourney.microservicechallenges.solution.domain.model.queries.GetSolutionByStudentIdAndChallengeIdQuery;
import com.levelupjourney.microservicechallenges.solution.domain.model.queries.GetSolutionsByChallengeIdQuery;
import com.levelupjourney.microservicechallenges.solution.domain.model.queries.GetSolutionsByStudentIdQuery;

import java.util.List;
import java.util.Optional;

public interface SolutionQueryService {
    public List<Solution> handle(GetAllSolutionsQuery query);
    public Optional<Solution> handle(GetSolutionByIdQuery query);
    public Optional<Solution> handle(GetSolutionByStudentIdAndChallengeIdQuery query);
    public List<Solution> handle(GetSolutionsByChallengeIdQuery query);
    public List<Solution> handle(GetSolutionsByStudentIdQuery query);
}
