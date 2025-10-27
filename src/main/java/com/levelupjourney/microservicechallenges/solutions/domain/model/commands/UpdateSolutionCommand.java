package com.levelupjourney.microservicechallenges.solutions.domain.model.commands;

import com.levelupjourney.microservicechallenges.solutions.domain.model.valueobjects.SolutionId;

/**
 * Command to update a solution's code.
 * 
 * <p>This command represents the intent to modify the source code of an existing solution.
 * It follows CQRS principles by encapsulating all necessary information for the command execution
 * and enforcing business invariants at creation time.</p>
 * 
 * <h3>Business Rules:</h3>
 * <ul>
 *   <li>Solution ID must not be null</li>
 *   <li>Code must not be null or blank</li>
 *   <li>Code must contain meaningful content (not just whitespace)</li>
 *   <li>Code length should be reasonable (min 1 char, max 50,000 chars)</li>
 * </ul>
 * 
 * <h3>Domain Events:</h3>
 * <p>This command may trigger:</p>
 * <ul>
 *   <li>SolutionCodeUpdatedEvent - when code is successfully modified</li>
 * </ul>
 * 
 * @param solutionId The unique identifier of the solution to update (must not be null)
 * @param code The new source code content (must not be blank, 1-50,000 characters)
 * 
 * @throws IllegalArgumentException if any business rule is violated
 */
public record UpdateSolutionCommand(
        SolutionId solutionId, 
        String code
) {
    
    // Constants for validation
    private static final int MIN_CODE_LENGTH = 1;
    private static final int MAX_CODE_LENGTH = 50_000; // 50KB should be enough for any solution
    
    /**
     * Compact constructor enforces invariants at command creation time.
     * This is a key DDD principle: commands should be self-validating.
     */
    public UpdateSolutionCommand {
        // Validate solution ID
        if (solutionId == null) {
            throw new IllegalArgumentException("Solution ID cannot be null");
        }
        
        // Validate code presence
        if (code == null) {
            throw new IllegalArgumentException("Code cannot be null");
        }
        
        // Trim whitespace
        code = code.trim();
        
        // Validate code is not empty after trimming
        if (code.isBlank()) {
            throw new IllegalArgumentException("Code cannot be empty or contain only whitespace");
        }
        
        // Validate code length
        if (code.length() < MIN_CODE_LENGTH) {
            throw new IllegalArgumentException(
                String.format("Code must be at least %d character(s)", MIN_CODE_LENGTH)
            );
        }
        
        if (code.length() > MAX_CODE_LENGTH) {
            throw new IllegalArgumentException(
                String.format("Code cannot exceed %d characters (current: %d)", 
                    MAX_CODE_LENGTH, code.length())
            );
        }
    }
    
    /**
     * Factory method for creating the command with explicit validation messaging.
     * Use this when you want to provide more context in error messages.
     * 
     * @param solutionId The solution identifier
     * @param code The new code content
     * @return A validated UpdateSolutionCommand instance
     * @throws IllegalArgumentException if validation fails
     */
    public static UpdateSolutionCommand create(SolutionId solutionId, String code) {
        return new UpdateSolutionCommand(solutionId, code);
    }
}
