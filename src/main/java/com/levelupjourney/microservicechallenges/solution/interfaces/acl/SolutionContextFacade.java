package com.levelupjourney.microservicechallenges.solution.interfaces.acl;

import java.util.List;

/**
 * Solution Context Facade - Anti-Corruption Layer
 * 
 * Provides simple access to solution operations for other bounded contexts.
 */
public interface SolutionContextFacade {
    
    /** Create new solution. Returns solution ID or null if failed. */
    String createSolution(String challengeId, String studentId, String code, String language);
    
    /** Submit solution for evaluation. Returns solution ID or null if failed. */
    String submitSolution(String challengeId, String studentId, String code, String language);
    
    /** Update existing solution. Returns true if successful, false if failed. */
    boolean updateSolution(String solutionId, String code, String language);
    
    /** Get solution basic info by ID. Returns info or null if not found. */
    SolutionBasicInfo fetchSolutionBasicInfoById(String solutionId);
    
    /** Check if student has solution for challenge. Returns true/false. */
    boolean hasSolutionForChallenge(String studentId, String challengeId);
    
    /** Get solution ID by student and challenge. Returns ID or null if not found. */
    String fetchSolutionIdByStudentAndChallenge(String studentId, String challengeId);
    
    /** Get all solution IDs for challenge. Returns list (empty if none found). */
    List<String> fetchSolutionIdsByChallenge(String challengeId);
    
    /** Get all solution IDs for student. Returns list (empty if none found). */
    List<String> fetchSolutionIdsByStudent(String studentId);
    
    /** Get solutions count for challenge. Returns number (0 if none). */
    int getSolutionsCountByChallenge(String challengeId);
    
    /** Get solutions count for student. Returns number (0 if none). */
    int getSolutionsCountByStudent(String studentId);
    
    /** Basic solution information for external contexts. */
    record SolutionBasicInfo(
        String id,
        String challengeId,
        String studentId,
        String language,
        String submittedAt,
        boolean isSubmitted
    ) {}
}
