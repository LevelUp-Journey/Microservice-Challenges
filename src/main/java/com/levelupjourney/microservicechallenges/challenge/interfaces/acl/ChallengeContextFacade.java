package com.levelupjourney.microservicechallenges.challenge.interfaces.acl;

import java.util.List;

/**
 * Challenge Context Facade - Anti-Corruption Layer
 * Provides simple access to challenge operations for other bounded contexts.
 */
public interface ChallengeContextFacade {
    
    /** Create a new challenge. Returns challenge ID or null if failed. */
    String createChallenge(String title, String description, String difficulty, String teacherId);
    
    /** Get challenge basic information by ID. Returns null if not found. */
    ChallengeBasicInfo fetchChallengeBasicInfoById(String challengeId);
    
    /** Check if challenge is published. Returns true/false. */
    boolean isChallengePublished(String challengeId);
    
    /** Get all published challenge IDs. Returns list (empty if none found). */
    List<String> fetchPublishedChallengeIds();
    
    /** Get all challenge IDs created by a teacher. Returns list (empty if none found). */
    List<String> fetchChallengeIdsByTeacherId(String teacherId);
    
    /** Student adds star to challenge. Returns true if successful, false if failed. */
    boolean starChallenge(String challengeId, String studentId);
    
    /** Student removes star from challenge. Returns true if successful, false if failed. */
    boolean unstarChallenge(String challengeId, String studentId);
    
    /** Get total stars count for challenge. Returns number (0 if none or not found). */
    int getChallengeStarsCount(String challengeId);
    
    /** Get total tests count for challenge. Returns number (0 if none or not found). */
    int getChallengeTestsCount(String challengeId);
    
    /** Basic challenge information for external contexts. */
    record ChallengeBasicInfo(
        String id,
        String title,
        String description,
        String difficulty,
        String state,
        String teacherId
    ) {}
}
