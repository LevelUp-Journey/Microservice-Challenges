package com.levelupjourney.microservicechallenges.solutions.interfaces.rest.transform;

import com.levelupjourney.microservicechallenges.solutions.domain.model.commands.UpdateSolutionCommand;
import com.levelupjourney.microservicechallenges.solutions.domain.model.valueobjects.SolutionId;
import com.levelupjourney.microservicechallenges.solutions.interfaces.rest.resource.UpdateSolutionResource;

import java.util.UUID;

/**
 * Assembler for transforming REST resources into domain commands.
 * 
 * <p>This assembler follows the Anti-Corruption Layer pattern from DDD,
 * ensuring that the domain layer remains independent from the presentation layer.
 * It translates HTTP-level concerns into domain-level commands.</p>
 * 
 * <h3>Responsibilities:</h3>
 * <ul>
 *   <li>Transform UpdateSolutionResource (REST) → UpdateSolutionCommand (Domain)</li>
 *   <li>Parse and validate UUID format from path parameters</li>
 *   <li>Ensure trimmed code content is passed to domain</li>
 *   <li>Provide clear error messages for invalid transformations</li>
 * </ul>
 * 
 * <h3>Design Notes:</h3>
 * <p>This assembler is stateless and uses static methods for simplicity.
 * All validation logic is delegated to the command's compact constructor,
 * following the principle of "parse, don't validate".</p>
 */
public final class UpdateSolutionCommandFromResourceAssembler {
    
    // Private constructor to prevent instantiation
    private UpdateSolutionCommandFromResourceAssembler() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * Transforms a REST resource and solution ID into a domain command.
     * 
     * <p>This method performs the following transformations:</p>
     * <ol>
     *   <li>Parses the solution ID string into a UUID</li>
     *   <li>Creates a SolutionId value object</li>
     *   <li>Extracts and trims the code from the resource</li>
     *   <li>Constructs a validated UpdateSolutionCommand</li>
     * </ol>
     * 
     * @param solutionId The solution UUID as a string (from path parameter)
     * @param resource The REST resource containing the new code
     * @return A validated domain command ready for execution
     * @throws IllegalArgumentException if the solution ID format is invalid
     * @throws IllegalArgumentException if the resource fails command validation
     * @throws NullPointerException if either parameter is null
     * 
     * @see UpdateSolutionCommand#UpdateSolutionCommand(SolutionId, String)
     */
    public static UpdateSolutionCommand toCommandFromResource(
            String solutionId, 
            UpdateSolutionResource resource) {
        
        // Defensive null checks
        if (solutionId == null || solutionId.isBlank()) {
            throw new IllegalArgumentException("Solution ID cannot be null or blank");
        }
        
        if (resource == null) {
            throw new IllegalArgumentException("Update solution resource cannot be null");
        }
        
        // Parse UUID with enhanced error handling
        UUID uuid;
        try {
            uuid = UUID.fromString(solutionId.trim());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                String.format("Invalid solution ID format: '%s'. Expected a valid UUID.", solutionId),
                e
            );
        }
        
        // Create value object
        SolutionId solutionIdVO = new SolutionId(uuid);
        
        // Extract and trim code
        String trimmedCode = resource.trimmedCode();
        
        // Create and return command (command constructor will validate)
        return UpdateSolutionCommand.create(solutionIdVO, trimmedCode);
    }
    
    /**
     * Alternative factory method that accepts a UUID directly.
     * Useful when the controller has already parsed the UUID.
     * 
     * @param solutionId The solution UUID (already parsed)
     * @param resource The REST resource containing the new code
     * @return A validated domain command
     * @throws IllegalArgumentException if resource validation fails
     * @throws NullPointerException if either parameter is null
     */
    public static UpdateSolutionCommand toCommandFromResource(
            UUID solutionId, 
            UpdateSolutionResource resource) {
        
        if (solutionId == null) {
            throw new IllegalArgumentException("Solution ID UUID cannot be null");
        }
        
        if (resource == null) {
            throw new IllegalArgumentException("Update solution resource cannot be null");
        }
        
        SolutionId solutionIdVO = new SolutionId(solutionId);
        String trimmedCode = resource.trimmedCode();
        
        return UpdateSolutionCommand.create(solutionIdVO, trimmedCode);
    }
}