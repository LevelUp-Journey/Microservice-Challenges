package com.levelupjourney.microservicechallenges.solutions.application.internal.commandservices;

import com.levelupjourney.microservicechallenges.solutions.application.internal.outboundservices.acl.ExternalChallengesService;
import com.levelupjourney.microservicechallenges.solutions.application.internal.outboundservices.grpc.CodeExecutionGrpcService;
import com.levelupjourney.microservicechallenges.solutions.domain.model.aggregates.Solution;
import com.levelupjourney.microservicechallenges.solutions.domain.model.commands.CreateSolutionCommand;
import com.levelupjourney.microservicechallenges.solutions.domain.model.commands.SubmitSolutionCommand;
import com.levelupjourney.microservicechallenges.solutions.domain.model.commands.UpdateSolutionCommand;
import com.levelupjourney.microservicechallenges.solutions.domain.model.queries.GetSolutionByIdQuery;
import com.levelupjourney.microservicechallenges.solutions.domain.model.valueobjects.SolutionReportId;
import com.levelupjourney.microservicechallenges.solutions.domain.services.SolutionCommandService;
import com.levelupjourney.microservicechallenges.solutions.domain.services.SolutionQueryService;
import com.levelupjourney.microservicechallenges.solutions.infrastructure.persistence.jpa.repositories.SolutionRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class SolutionCommandServiceImpl implements SolutionCommandService {
    private final ExternalChallengesService externalChallengesService;
    private final SolutionQueryService solutionQueryService;
    private final SolutionRepository solutionRepository;
    private final CodeExecutionGrpcService codeExecutionGrpcService;

    public SolutionCommandServiceImpl(ExternalChallengesService externalChallengesService, 
                                    SolutionQueryService solutionQueryService,
                                    SolutionRepository solutionRepository,
                                    CodeExecutionGrpcService codeExecutionGrpcService) {
        this.externalChallengesService = externalChallengesService;
        this.solutionQueryService = solutionQueryService;
        this.solutionRepository = solutionRepository;
        this.codeExecutionGrpcService = codeExecutionGrpcService;
    }

    @Override
    public Optional<Solution> handle(CreateSolutionCommand command) {
        var solution = new Solution(command);
        var savedSolution = solutionRepository.save(solution);
        return Optional.of(savedSolution);
    }

    @Override
    public Optional<SolutionReportId> handle(SubmitSolutionCommand command) {
        // 1. Verify solution exists and get its CodeVersion
        var solution = solutionQueryService.handle(
                new GetSolutionByIdQuery(command.solutionId())
        );

        if (solution.isEmpty()) {
            throw new IllegalArgumentException("Solution not found: " + command.solutionId().id());
        }

        var existingSolution = solution.get();

        // 2. Get code version details (language + tests) through ACL
        var codeVersionDetails = externalChallengesService.getCodeVersionDetailsForSubmission(
            existingSolution.getCodeVersionId().id().toString()
        );

        // 3. Submit solution for execution using CodeRunner with all required data
        var executionResult = codeExecutionGrpcService.executeCodeWithTests(
            command.solutionId().id().toString(),
            existingSolution.getCodeVersionId().id().toString(),
            command.studentId().id().toString(),
            command.code(),
            codeVersionDetails.codeLanguage(), // Use language from CodeVersion
            codeVersionDetails.tests() // Pass all tests to CodeRunner
        );

        if (executionResult.isSuccess()) {
            // Create solution report with approved test IDs
            var approvedTestIds = executionResult.getApprovedTestIds();
            // TODO: Create actual SolutionReport entity with approved test IDs
            return Optional.of(new SolutionReportId(UUID.randomUUID()));
        }

        return Optional.empty();
    }

    @Override
    @Transactional
    public void handle(UpdateSolutionCommand command) {
        var solution = solutionQueryService.handle(
                new GetSolutionByIdQuery(command.solutionId())
        );

        if (solution.isEmpty()) {
            throw new IllegalArgumentException("Solution not found: " + command.solutionId().id());
        }

        var existingSolution = solution.get();
        existingSolution.updateSolution(command.code(), command.language());
        solutionRepository.save(existingSolution);
    }
}
