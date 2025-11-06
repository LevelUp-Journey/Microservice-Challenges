package com.levelupjourney.microservicechallenges.solutions.interfaces.rest.resource;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * REST resource for updating a solution's code.
 * 
 * <p>This resource represents the HTTP request body for the update solution endpoint.
 * It follows REST best practices by including comprehensive validation annotations
 * and clear API documentation.</p>
 * 
 * <h3>Validation Rules:</h3>
 * <ul>
 *   <li>Code field is mandatory and cannot be blank</li>
 *   <li>Code must be between 1 and 50,000 characters</li>
 *   <li>Whitespace-only code is rejected</li>
 * </ul>
 * 
 * <h3>Usage Example:</h3>
 * <pre>
 * PUT /api/v1/solutions/{solutionId}
 * Content-Type: application/json
 * 
 * {
 *   "code": "function solve(n) {\n  return n * 2;\n}"
 * }
 * </pre>
 * 
 * @param code The updated source code for the solution (required, 1-50,000 characters)
 */
@Schema(
    name = "UpdateSolutionResource",
    description = """
        Request body for updating a solution's code.
        
        This resource allows students to modify their solution implementation.
        Only the code field can be updated through this endpoint.
        Other solution properties (status, attempts, scores) are managed through separate endpoints.
        
        The code will be validated for:
        - Non-null and non-blank content
        - Reasonable length constraints (1-50,000 characters)
        - Meaningful content (not just whitespace)
        """
)
public record UpdateSolutionResource(
        
        @NotNull(message = "Code field is required")
        @NotBlank(message = "Code cannot be empty or contain only whitespace")
        @Size(
            min = 1, 
            max = 50_000, 
            message = "Code must be between 1 and 50,000 characters"
        )
        @Schema(
            description = """
                The updated source code for the solution.
                
                This should contain the complete implementation of the student's solution.
                The code will be persisted as-is, so ensure it includes all necessary formatting.
                
                Supported languages depend on the associated challenge's code version.
                Common languages: JavaScript, Python, Java, C++, TypeScript.
                """,
            example = """
                function solve(n) {
                  // Check if n is prime
                  if (n < 2) return false;
                  
                  for (let i = 2; i <= Math.sqrt(n); i++) {
                    if (n % i === 0) return false;
                  }
                  
                  return true;
                }
                """,
            minLength = 1,
            maxLength = 50_000,
            required = true
        )
        String code
) {
    
    /**
     * Compact constructor for additional runtime validation.
     * This provides defense-in-depth beyond the annotation-based validation.
     */
    public UpdateSolutionResource {
        // Defensive programming: ensure code is never null
        if (code == null) {
            throw new IllegalArgumentException("Code cannot be null");
        }
        
        // Additional business validation can be added here
        // For example: checking for suspicious patterns, SQL injection attempts, etc.
    }
    
    /**
     * Factory method for creating the resource with explicit validation.
     * 
     * @param code The solution code
     * @return A validated UpdateSolutionResource instance
     * @throws IllegalArgumentException if validation fails
     */
    public static UpdateSolutionResource of(String code) {
        return new UpdateSolutionResource(code);
    }
    
    /**
     * Provides a trimmed version of the code.
     * Useful for preprocessing before passing to the command layer.
     * 
     * @return The code with leading and trailing whitespace removed
     */
    public String trimmedCode() {
        return code.trim();
    }
}