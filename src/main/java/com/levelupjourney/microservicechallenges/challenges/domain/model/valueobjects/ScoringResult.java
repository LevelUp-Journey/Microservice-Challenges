package com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects;

/**
 * Scoring Result
 * <p>
 * Encapsulates the result of a time-based score calculation.
 * Provides detailed information about how the score was calculated including
 * base score, time penalties, and final awarded score.
 *
 * @param baseScore          Maximum possible points for the challenge
 * @param timeTakenSeconds   Time taken to complete the challenge
 * @param scoreMultiplier    Score multiplier applied based on time (100 = 100%, 80 = 80%, etc.)
 * @param finalScore         Final score awarded after applying time penalties
 * @param penaltyApplied     Whether a time penalty was applied
 */
public record ScoringResult(
        int baseScore,
        long timeTakenSeconds,
        int scoreMultiplier,
        int finalScore,
        boolean penaltyApplied
) {
    /**
     * Create scoring result with time-based penalty calculation
     *
     * @param baseScore        Maximum possible points for the challenge
     * @param timeTakenSeconds Time taken to complete the challenge
     * @param scoreMultiplier  Score multiplier as percentage (100 = 100%, 80 = 80%, etc.)
     * @return ScoringResult instance
     */
    public static ScoringResult withTimeBasedPenalty(int baseScore, long timeTakenSeconds, int scoreMultiplier) {
        int finalScore = (baseScore * scoreMultiplier) / 100;
        boolean penaltyApplied = scoreMultiplier < 100;
        return new ScoringResult(baseScore, timeTakenSeconds, scoreMultiplier, finalScore, penaltyApplied);
    }

    /**
     * Create scoring result without time penalty (full score)
     *
     * @param baseScore        Maximum possible points for the challenge
     * @param timeTakenSeconds Time taken to complete the challenge
     * @return ScoringResult instance with full score
     */
    public static ScoringResult withoutPenalty(int baseScore, long timeTakenSeconds) {
        return new ScoringResult(baseScore, timeTakenSeconds, 100, baseScore, false);
    }

    /**
     * Validate scoring result values
     */
    public ScoringResult {
        if (baseScore < 0) {
            throw new IllegalArgumentException("Base score cannot be negative");
        }
        if (timeTakenSeconds < 0) {
            throw new IllegalArgumentException("Time taken cannot be negative");
        }
        if (scoreMultiplier < 0 || scoreMultiplier > 100) {
            throw new IllegalArgumentException("Score multiplier must be between 0 and 100");
        }
        if (finalScore < 0 || finalScore > baseScore) {
            throw new IllegalArgumentException("Final score must be between 0 and base score");
        }
    }

    /**
     * Get time taken in minutes for display purposes
     *
     * @return Time taken rounded to nearest minute
     */
    public long getTimeTakenMinutes() {
        return (timeTakenSeconds + 30) / 60; // Round to nearest minute
    }

    /**
     * Get formatted time taken as "MM:SS"
     *
     * @return Formatted time string
     */
    public String getFormattedTime() {
        long minutes = timeTakenSeconds / 60;
        long seconds = timeTakenSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    /**
     * Get score percentage achieved
     *
     * @return Percentage of maximum score achieved (0-100)
     */
    public int getScorePercentage() {
        if (baseScore == 0) return 0;
        return (finalScore * 100) / baseScore;
    }

    /**
     * Generate a human-readable explanation of the scoring result
     *
     * @param difficulty The challenge difficulty level
     * @return Detailed scoring reason
     */
    public String getScoringReason(String difficulty) {
        if (baseScore == 0) {
            return "No score available for this challenge";
        }

        if (!penaltyApplied) {
            return String.format(
                "Full score awarded: Completed within grace period (%s)",
                getFormattedTime()
            );
        }

        String timeRange = getTimeRangeDescription(difficulty);
        return String.format(
            "Score: %d/%d points (%d%% multiplier applied). Completed in %s. %s",
            finalScore,
            baseScore,
            scoreMultiplier,
            getFormattedTime(),
            timeRange
        );
    }

    /**
     * Get time range description based on difficulty and multiplier
     */
    private String getTimeRangeDescription(String difficulty) {
        return switch (scoreMultiplier) {
            case 100 -> "Excellent time! Completed within grace period.";
            case 80 -> String.format("Good time for %s difficulty. Small penalty applied.", difficulty);
            case 60 -> String.format("Moderate time for %s difficulty. Medium penalty applied.", difficulty);
            case 40 -> String.format("Slower completion for %s difficulty. Heavy penalty applied.", difficulty);
            case 20 -> String.format("Significant time taken for %s difficulty. Maximum penalty applied but minimum score guaranteed.", difficulty);
            default -> String.format("Time penalty applied: %d%% multiplier.", scoreMultiplier);
        };
    }
}
