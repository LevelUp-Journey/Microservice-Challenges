package com.levelupjourney.microservicechallenges.solutions.application.internal.commandservices;

import com.levelupjourney.microservicechallenges.solutions.application.internal.outboundservices.acl.ExternalChallengesService;
import com.levelupjourney.microservicechallenges.solutions.domain.model.aggregates.Solution;
import com.levelupjourney.microservicechallenges.solutions.domain.model.commands.CreateSolutionCommand;
import com.levelupjourney.microservicechallenges.solutions.domain.model.commands.SubmitSolutionCommand;
import com.levelupjourney.microservicechallenges.solutions.domain.model.commands.UpdateSolutionCommand;
import com.levelupjourney.microservicechallenges.solutions.domain.model.queries.GetSolutionByIdQuery;
import com.levelupjourney.microservicechallenges.solutions.domain.model.queries.GetSolutionByStudentIdAndCodeVersionIdQuery;
import com.levelupjourney.microservicechallenges.solutions.domain.model.valueobjects.SolutionReportId;
import com.levelupjourney.microservicechallenges.solutions.domain.services.SolutionCommandService;
import com.levelupjourney.microservicechallenges.solutions.domain.services.SolutionQueryService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SolutionCommandServiceImpl implements SolutionCommandService {
    private ExternalChallengesService externalChallengesService;
    private SolutionQueryService solutionQueryService;

    public SolutionCommandServiceImpl(ExternalChallengesService externalChallengesService) {
        this.externalChallengesService = externalChallengesService;
    }

    @Override
    public Optional<Solution> handle(CreateSolutionCommand command) {
        return Optional.empty();
    }

    @Override
    public SolutionReportId handle(SubmitSolutionCommand command) {

        var solution = solutionQueryService.handle(
                new GetSolutionByIdQuery(command.solutionId())
        );

        if (solution.isEmpty()) {
            throw new IllegalArgumentException("Solution not found");
        }
        // Fetch code version details from external service
        var codeVersionId = solution.get().getCodeVersionId();
        var codeVersionDetails = externalChallengesService.fetchCodeVersionDetailsForSubmittingByCodeVersionId(codeVersionId.toString());

        // Submit solution to external service


        return null;
    }

    @Override
    public void handle(UpdateSolutionCommand command) {

    }

}
