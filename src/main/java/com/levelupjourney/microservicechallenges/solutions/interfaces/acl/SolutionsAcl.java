package com.levelupjourney.microservicechallenges.solutions.interfaces.acl;

import com.levelupjourney.microservicechallenges.solutions.domain.model.commands.CreateSolutionCommand;
import com.levelupjourney.microservicechallenges.solutions.domain.model.queries.GetSolutionByChallengeIdAndCodeVersionIdAndStudentIdQuery;
import com.levelupjourney.microservicechallenges.solutions.domain.model.valueobjects.ChallengeId;
import com.levelupjourney.microservicechallenges.solutions.domain.model.valueobjects.CodeVersionId;
import com.levelupjourney.microservicechallenges.solutions.domain.model.valueobjects.StudentId;
import com.levelupjourney.microservicechallenges.solutions.domain.services.SolutionCommandService;
import com.levelupjourney.microservicechallenges.solutions.domain.services.SolutionQueryService;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Anti-Corruption Layer for Solutions bounded context.
 * Provides a stable interface for other bounded contexts to interact with solutions.
 * Uses primitive types (String, UUID) in method signatures to maintain decoupling.
 */
@Component
public class SolutionsAcl {

    private final SolutionCommandService solutionCommandService;
    private final SolutionQueryService solutionQueryService;

    public SolutionsAcl(SolutionCommandService solutionCommandService,
                       SolutionQueryService solutionQueryService) {
        this.solutionCommandService = solutionCommandService;
        this.solutionQueryService = solutionQueryService;
    }

    /**
     * Checks if a solution already exists for a student on a specific challenge and code version.
     * This is used to prevent duplicate solutions when starting a challenge.
     * 
     * @param studentId Student UUID as String
     * @param challengeId Challenge UUID as String
     * @param codeVersionId Code Version UUID as String
     * @return true if solution exists, false otherwise
     */
    public boolean solutionExistsForStudentAndChallenge(
            String studentId,
            String challengeId,
            String codeVersionId) {
        
        // Convert primitive types to Solutions bounded context value objects
        StudentId solutionsStudentId = new StudentId(UUID.fromString(studentId));
        ChallengeId solutionsChallengeId = new ChallengeId(UUID.fromString(challengeId));
        CodeVersionId solutionsCodeVersionId = new CodeVersionId(UUID.fromString(codeVersionId));

        // Create query to check if solution exists
        GetSolutionByChallengeIdAndCodeVersionIdAndStudentIdQuery query = 
            new GetSolutionByChallengeIdAndCodeVersionIdAndStudentIdQuery(
                solutionsChallengeId,
                solutionsCodeVersionId,
                solutionsStudentId
            );

        // Execute query and check if solution exists
        return solutionQueryService.handle(query).isPresent();
    }

    /**
     * Gets an existing solution for a student on a specific challenge and code version.
     * Returns the solution ID if it exists, null otherwise.
     * 
     * @param studentId Student UUID as String
     * @param challengeId Challenge UUID as String
     * @param codeVersionId Code Version UUID as String
     * @return Solution ID as String, or null if not found
     */
    public String getExistingSolutionId(
            String studentId,
            String challengeId,
            String codeVersionId) {
        
        // Convert primitive types to Solutions bounded context value objects
        StudentId solutionsStudentId = new StudentId(UUID.fromString(studentId));
        ChallengeId solutionsChallengeId = new ChallengeId(UUID.fromString(challengeId));
        CodeVersionId solutionsCodeVersionId = new CodeVersionId(UUID.fromString(codeVersionId));

        // Create query to get solution
        GetSolutionByChallengeIdAndCodeVersionIdAndStudentIdQuery query = 
            new GetSolutionByChallengeIdAndCodeVersionIdAndStudentIdQuery(
                solutionsChallengeId,
                solutionsCodeVersionId,
                solutionsStudentId
            );

        // Execute query and return solution ID if present
        return solutionQueryService.handle(query)
            .map(solution -> solution.getId().id().toString())
            .orElse(null);
    }

    /**
     * Creates a default solution for a student starting a challenge.
     * This is called when a ChallengeStartedEvent is published.
     * 
     * @param studentId Student UUID as String
     * @param challengeId Challenge UUID as String
     * @param codeVersionId Code Version UUID as String
     * @param defaultCode Initial code template
     */
    public void createDefaultSolution(String studentId, String challengeId, String codeVersionId, String defaultCode) {
        // Convert primitive types to Solutions bounded context value objects
        StudentId solutionsStudentId = new StudentId(UUID.fromString(studentId));
        ChallengeId solutionsChallengeId = new ChallengeId(UUID.fromString(challengeId));
        CodeVersionId solutionsCodeVersionId = new CodeVersionId(UUID.fromString(codeVersionId));

        CreateSolutionCommand command = new CreateSolutionCommand(
            solutionsChallengeId,
            solutionsCodeVersionId,
            solutionsStudentId,
            defaultCode
        );

        solutionCommandService.handle(command);
    }
}