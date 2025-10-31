package com.levelupjourney.microservicechallenges.challenges.application.internal.services;

import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.Difficulty;
import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.ScoringResult;
import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.TimeBasedScoringConfig;
import com.levelupjourney.microservicechallenges.challenges.domain.services.TimeBasedScoringStrategy;
import org.springframework.stereotype.Service;

/**
 * Time-Based Scoring Strategy Implementation
 * <p>
 * Implements difficulty-aware time-based scoring with progressive penalties.
 * <p>
 * Scoring Rules:
 * 1. Only apply time penalties if ALL tests pass
 * 2. If not all tests pass, use binary scoring (0 points)
 * 3. Progressive penalty system based on difficulty:
 *    - EASY: 10 min (100%), 20 min (80%), 30 min (60%), 40 min (40%), >40 min (20%)
 *    - MEDIUM: 20 min (100%), 40 min (80%), 60 min (60%), 90 min (40%), >90 min (20%)
 *    - HARD: 30 min (100%), 60 min (80%), 90 min (60%), 120 min (40%), >120 min (20%)
 *    - EXPERT: 60 min (100%), 120 min (80%), 180 min (60%), 240 min (40%), >240 min (20%)
 * 4. Minimum score is always 20% of base score (never zero if completed)
 */
@Service
public class TimeBasedScoringStrategyImpl implements TimeBasedScoringStrategy {

    /**
     * Calculate score with time-based penalties
     * <p>
     * Time penalties are only applied when all tests pass. If tests are incomplete,
     * the standard binary scoring applies (0 points).
     *
     * @param baseScore        Maximum points available for the challenge
     * @param difficulty       Challenge difficulty level
     * @param timeTakenSeconds Time taken to complete the challenge (in seconds)
     * @param allTestsPassed   Whether all tests passed (penalties only apply if true)
     * @return ScoringResult containing final score and calculation details
     */
    @Override
    public ScoringResult calculateScore(int baseScore, Difficulty difficulty, long timeTakenSeconds, boolean allTestsPassed) {
        // Validate input parameters
        if (baseScore < 0) {
            throw new IllegalArgumentException("Base score cannot be negative");
        }
        if (timeTakenSeconds < 0) {
            throw new IllegalArgumentException("Time taken cannot be negative");
        }
        if (difficulty == null) {
            throw new IllegalArgumentException("Difficulty cannot be null");
        }

        // If base score is zero, return zero score
        if (baseScore == 0) {
            return ScoringResult.withoutPenalty(0, timeTakenSeconds);
        }

        // Only apply time-based penalties if all tests passed
        if (!allTestsPassed) {
            // Binary scoring: no points if not all tests pass
            return new ScoringResult(baseScore, timeTakenSeconds, 0, 0, false);
        }

        // Get time-based scoring configuration for the difficulty level
        TimeBasedScoringConfig config = TimeBasedScoringConfig.forDifficulty(difficulty);

        // Calculate score multiplier based on time taken
        int scoreMultiplier = config.calculateScoreMultiplier(timeTakenSeconds);

        // Calculate final score with time penalty
        return ScoringResult.withTimeBasedPenalty(baseScore, timeTakenSeconds, scoreMultiplier);
    }

    /**
     * Calculate score without time penalty
     * <p>
     * This method maintains backward compatibility with the original scoring system.
     * It uses binary scoring: full points if all tests pass, zero points otherwise.
     * <p>
     * Note: The proportional scoring strategy (partial credit) is not currently active
     * but could be enabled in the future if business requirements change.
     *
     * @param baseScore      Maximum points available for the challenge
     * @param passedTests    Number of tests passed
     * @param totalTests     Total number of tests
     * @param allTestsPassed Whether all tests passed
     * @return Final score without time penalty
     */
    @Override
    public int calculateScoreWithoutTimePenalty(int baseScore, int passedTests, int totalTests, boolean allTestsPassed) {
        // Validate input parameters
        if (baseScore < 0) {
            return 0;
        }
        if (totalTests == 0) {
            return 0;
        }

        // Strategy 1: Full points only if all tests pass (CURRENTLY ACTIVE)
        if (allTestsPassed) {
            return baseScore;
        }

        // Strategy 2: Proportional scoring (COMMENTED OUT - NOT ACTIVE)
        // This strategy would award partial credit based on the number of tests passed
        // return (baseScore * passedTests) / totalTests;

        // Default: No points if not all tests pass
        return 0;
    }
}
