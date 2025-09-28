package com.levelupjourney.microservicechallenges.solutions.interfaces.acl;

import com.levelupjourney.microservicechallenges.solutions.domain.model.commands.CreateSolutionCommand;
import com.levelupjourney.microservicechallenges.solutions.domain.model.valueobjects.ChallengeId;
import com.levelupjourney.microservicechallenges.solutions.domain.model.valueobjects.CodeVersionId;
import com.levelupjourney.microservicechallenges.solutions.domain.model.valueobjects.StudentId;
import com.levelupjourney.microservicechallenges.solutions.domain.services.SolutionCommandService;
import org.springframework.stereotype.Component;

/**
 * Anti-Corruption Layer for Solutions bounded context.
 * Provides a stable interface for other bounded contexts to interact with solutions.
 */
@Component
public class SolutionsAcl {

    private final SolutionCommandService solutionCommandService;

    public SolutionsAcl(SolutionCommandService solutionCommandService) {
        this.solutionCommandService = solutionCommandService;
    }

    /**
     * Creates a default solution for a student starting a challenge.
     * This is called when a ChallengeStartedEvent is published.
     * Converts value objects from challenges bounded context to solutions bounded context.
     */
    public void createDefaultSolution(com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.StudentId studentId,
                                    com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.ChallengeId challengeId,
                                    com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.CodeVersionId codeVersionId,
                                    String defaultCode) {
        // Convert value objects between bounded contexts
        StudentId solutionsStudentId = new StudentId(studentId.id());
        ChallengeId solutionsChallengeId = new ChallengeId(challengeId.id());
        CodeVersionId solutionsCodeVersionId = new CodeVersionId(codeVersionId.id());

        CreateSolutionCommand command = new CreateSolutionCommand(
            solutionsChallengeId,
            solutionsCodeVersionId,
            solutionsStudentId,
            defaultCode
        );

        solutionCommandService.handle(command);
    }
}