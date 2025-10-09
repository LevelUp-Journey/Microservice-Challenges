package com.levelupjourney.microservicechallenges.solutions.application.internal.queryservices;

import com.levelupjourney.microservicechallenges.solutions.domain.model.aggregates.Solution;
import com.levelupjourney.microservicechallenges.solutions.domain.model.queries.GetSolutionByChallengeIdAndCodeVersionIdAndStudentIdQuery;
import com.levelupjourney.microservicechallenges.solutions.domain.model.queries.GetSolutionByIdQuery;
import com.levelupjourney.microservicechallenges.solutions.domain.model.queries.GetSolutionByStudentIdAndCodeVersionIdQuery;
import com.levelupjourney.microservicechallenges.solutions.domain.services.SolutionQueryService;
import com.levelupjourney.microservicechallenges.solutions.infrastructure.persistence.jpa.repositories.SolutionRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SolutionQueryServiceImpl implements SolutionQueryService {

    private final SolutionRepository solutionRepository;

    public SolutionQueryServiceImpl(SolutionRepository solutionRepository) {
        this.solutionRepository = solutionRepository;
    }

    @Override
    public Optional<Solution> handle(GetSolutionByIdQuery query) {
        return solutionRepository.findById(query.solutionId());
    }

    @Override
    public Optional<Solution> handle(GetSolutionByStudentIdAndCodeVersionIdQuery query) {
        return solutionRepository.findByStudentIdAndCodeVersionId(query.studentId().id(), query.codeVersionId().id());
    }

    @Override
    public Optional<Solution> handle(GetSolutionByChallengeIdAndCodeVersionIdAndStudentIdQuery query) {
        return solutionRepository.findByChallengeIdAndCodeVersionIdAndStudentId(
                query.challengeId().id(),
                query.codeVersionId().id(),
                query.studentId().id()
        );
    }
}
