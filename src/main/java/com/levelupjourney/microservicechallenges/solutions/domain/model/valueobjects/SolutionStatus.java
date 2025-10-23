package com.levelupjourney.microservicechallenges.solutions.domain.model.valueobjects;

/**
 * Represents the status of a student's solution
 */
public enum SolutionStatus {
    /**
     * Solution has not been tested yet (initial state)
     */
    NO_TESTED,
    
    /**
     * Solution is in progress (partially correct or still being worked on)
     */
    IN_PROGRESS,
    
    /**
     * Solution passed all tests successfully
     */
    SUCCESS,
    
    /**
     * Solution failed tests
     */
    FAILED,
    
    /**
     * Solution has reached maximum submission attempts
     */
    MAX_ATTEMPTS_REACHED
}
