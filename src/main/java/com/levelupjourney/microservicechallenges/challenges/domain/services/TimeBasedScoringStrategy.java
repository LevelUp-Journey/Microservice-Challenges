package com.levelupjourney.microservicechallenges.challenges.domain.services;

import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.Difficulty;
import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.ScoringResult;

/**
 * Time-Based Scoring Strategy
 * <p>
 * Domain service for calculating challenge scores with time-based penalties.
 * Implements difficulty-aware scoring where easier challenges have shorter grace periods
 * and steeper penalties, while harder challenges have longer grace periods and gentler penalties.
 * <p>
 * Business Rules:
 * - Score decreases progressively based on time taken
 * - Each difficulty level has its own time thresholds
 * - Minimum score is always guaranteed (20% of base score)
 * - Penalties only apply if all tests pass
 */
public interface TimeBasedScoringStrategy {

    /**
     * Calculate score with time-based penalties
     *
     * @param baseScore        Maximum points available for the challenge
     * @param difficulty       Challenge difficulty level
     * @param timeTakenSeconds Time taken to complete the challenge (in seconds)
     * @param allTestsPassed   Whether all tests passed (penalties only apply if true)
     * @return ScoringResult containing final score and calculation details
     */
    ScoringResult calculateScore(int baseScore, Difficulty difficulty, long timeTakenSeconds, boolean allTestsPassed);

    /**
     * Calculate score without time penalty (for partial test completion)
     *
     * @param baseScore      Maximum points available for the challenge
     * @param passedTests    Number of tests passed
     * @param totalTests     Total number of tests
     * @param allTestsPassed Whether all tests passed
     * @return Final score without time penalty
     */
    int calculateScoreWithoutTimePenalty(int baseScore, int passedTests, int totalTests, boolean allTestsPassed);
}
