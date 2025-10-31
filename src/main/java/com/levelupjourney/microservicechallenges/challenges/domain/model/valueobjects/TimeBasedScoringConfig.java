package com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects;

/**
 * Time-Based Scoring Configuration
 * <p>
 * Defines time thresholds and penalty rates for calculating difficulty-aware time-based scores.
 * Each difficulty level has its own grace period and penalty progression to ensure fair scoring.
 * <p>
 * Design Rationale:
 * - Easy challenges: Short grace period, steep penalties (expected quick resolution)
 * - Expert challenges: Long grace period, gentle penalties (naturally complex)
 * - Progressive penalties: Score decreases gradually, never reaches zero
 * - Minimum score: Always earn 20% of max points for completing the challenge
 *
 * @param graceTimeSeconds       Time before penalties start (100% score if completed within this time)
 * @param mediumPenaltySeconds   Time threshold for medium penalty (80% score multiplier)
 * @param heavyPenaltySeconds    Time threshold for heavy penalty (60% score multiplier)
 * @param maximumPenaltySeconds  Time threshold for maximum penalty (40% score multiplier)
 * @param minimumScorePercent    Minimum score percentage guaranteed (never goes below 20%)
 */
public record TimeBasedScoringConfig(
        long graceTimeSeconds,
        long mediumPenaltySeconds,
        long heavyPenaltySeconds,
        long maximumPenaltySeconds,
        int minimumScorePercent
) {
    /**
     * Factory method to create configuration for a specific difficulty level
     *
     * @param difficulty The challenge difficulty level
     * @return TimeBasedScoringConfig instance with appropriate thresholds
     */
    public static TimeBasedScoringConfig forDifficulty(Difficulty difficulty) {
        return switch (difficulty) {
            case EASY -> new TimeBasedScoringConfig(
                    600L,    // 10 minutes grace time (100% score)
                    1200L,   // 20 minutes (80% score)
                    1800L,   // 30 minutes (60% score)
                    2400L,   // 40 minutes (40% score)
                    20       // 20% minimum score
            );
            case MEDIUM -> new TimeBasedScoringConfig(
                    1200L,   // 20 minutes grace time (100% score)
                    2400L,   // 40 minutes (80% score)
                    3600L,   // 60 minutes (60% score)
                    5400L,   // 90 minutes (40% score)
                    20       // 20% minimum score
            );
            case HARD -> new TimeBasedScoringConfig(
                    1800L,   // 30 minutes grace time (100% score)
                    3600L,   // 60 minutes (80% score)
                    5400L,   // 90 minutes (60% score)
                    7200L,   // 120 minutes (40% score)
                    20       // 20% minimum score
            );
            case EXPERT -> new TimeBasedScoringConfig(
                    3600L,   // 60 minutes grace time (100% score)
                    7200L,   // 120 minutes (80% score)
                    10800L,  // 180 minutes (60% score)
                    14400L,  // 240 minutes (40% score)
                    20       // 20% minimum score
            );
        };
    }

    /**
     * Calculate score multiplier based on time taken
     *
     * @param timeTakenSeconds Time taken to complete the challenge
     * @return Score multiplier as a percentage (100 = 100%, 80 = 80%, etc.)
     */
    public int calculateScoreMultiplier(long timeTakenSeconds) {
        if (timeTakenSeconds <= graceTimeSeconds) {
            return 100; // Full score - within grace period
        } else if (timeTakenSeconds <= mediumPenaltySeconds) {
            return 80; // Medium penalty
        } else if (timeTakenSeconds <= heavyPenaltySeconds) {
            return 60; // Heavy penalty
        } else if (timeTakenSeconds <= maximumPenaltySeconds) {
            return 40; // Maximum penalty
        } else {
            return minimumScorePercent; // Minimum score guaranteed (20%)
        }
    }

    /**
     * Validate configuration values
     */
    public TimeBasedScoringConfig {
        if (graceTimeSeconds <= 0) {
            throw new IllegalArgumentException("Grace time must be positive");
        }
        if (mediumPenaltySeconds <= graceTimeSeconds) {
            throw new IllegalArgumentException("Medium penalty time must be greater than grace time");
        }
        if (heavyPenaltySeconds <= mediumPenaltySeconds) {
            throw new IllegalArgumentException("Heavy penalty time must be greater than medium penalty time");
        }
        if (maximumPenaltySeconds <= heavyPenaltySeconds) {
            throw new IllegalArgumentException("Maximum penalty time must be greater than heavy penalty time");
        }
        if (minimumScorePercent < 0 || minimumScorePercent > 100) {
            throw new IllegalArgumentException("Minimum score percent must be between 0 and 100");
        }
    }
}
