package com.levelupjourney.microservicechallenges.solutions.domain.model.valueobjects;

import jakarta.persistence.Embeddable;

/**
 * Value object representing the score earned by a student for completing a challenge solution.
 * Contains both the points earned based on test results and the maximum possible points.
 */
@Embeddable
public record SolutionScore(
    Integer pointsEarned,
    Integer maxPoints
) {
    /**
     * Default constructor with validation
     */
    public SolutionScore {
        if (pointsEarned != null && pointsEarned < 0) {
            throw new IllegalArgumentException("Points earned cannot be negative");
        }
        if (maxPoints != null && maxPoints < 0) {
            throw new IllegalArgumentException("Max points cannot be negative");
        }
        if (pointsEarned != null && maxPoints != null && pointsEarned > maxPoints) {
            throw new IllegalArgumentException("Points earned cannot exceed max points");
        }
    }

    /**
     * Default score for new solutions (not yet graded)
     */
    public static SolutionScore defaultScore() {
        return new SolutionScore(0, 0);
    }

    /**
     * Calculate success percentage
     */
    public double getSuccessPercentage() {
        if (maxPoints == null || maxPoints == 0) {
            return 0.0;
        }
        return (pointsEarned != null ? pointsEarned : 0) * 100.0 / maxPoints;
    }

    /**
     * Check if solution achieved full score
     */
    public boolean isFullScore() {
        return pointsEarned != null && maxPoints != null && pointsEarned.equals(maxPoints);
    }
}
