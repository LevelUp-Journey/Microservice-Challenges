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
import org.springframework.stereotype.Service;

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
        // 1. Verify solution exists
        var solution = solutionQueryService.handle(
                new GetSolutionByIdQuery(command.solutionId())
        );

        if (solution.isEmpty()) {
            throw new IllegalArgumentException("Solution not found: " + command.solutionId().value());
        }

        // 2. Get challenge details through ACL
        var challengeDetails = externalChallengesService.getChallengeDetailsToBeSubmitted(
            command.challengeId().value().toString()
        );

        if (challengeDetails == null) {
            throw new IllegalArgumentException("Challenge not found: " + command.challengeId().value());
        }

        // 3. Submit solution for execution through gRPC to CodeRunner microservice
        var executionResult = codeExecutionGrpcService.executeCode(
            command.solutionId().value().toString(),
            command.challengeId().value().toString(),
            command.studentId().value().toString(),
            command.code(),
            command.language(),
            command.comments()
        );

        if (executionResult.isSuccess()) {
            // Create solution report with execution ID
            return Optional.of(new SolutionReportId(UUID.fromString(executionResult.getExecutionId())));
        }

        return Optional.empty();
    }

    @Override
    public void handle(UpdateSolutionCommand command) {
        var solution = solutionQueryService.handle(
                new GetSolutionByIdQuery(command.solutionId())
        );

        if (solution.isEmpty()) {
            throw new IllegalArgumentException("Solution not found: " + command.solutionId().value());
        }

        var existingSolution = solution.get();
        existingSolution.updateSolution(command.code(), command.language());
        solutionRepository.save(existingSolution);
    }
}
