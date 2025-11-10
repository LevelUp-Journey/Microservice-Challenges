package com.levelupjourney.microservicechallenges.challenges.domain.model.queries;

/**
 * Query to search published challenges with optional filters.
 * Returns only challenges with PUBLISHED status.
 *
 * @param name Optional filter for challenge name (case-insensitive partial match)
 * @param difficulty Optional filter for difficulty level
 * @param tags Optional filter for tags (comma-separated)
 */
public record SearchPublishedChallengesQuery(
    String name,
    String difficulty,
    String tags
) {
    public boolean hasNameFilter() {
        return name != null && !name.trim().isEmpty();
    }

    public boolean hasDifficultyFilter() {
        return difficulty != null && !difficulty.trim().isEmpty();
    }

    public boolean hasTagsFilter() {
        return tags != null && !tags.trim().isEmpty();
    }
}
