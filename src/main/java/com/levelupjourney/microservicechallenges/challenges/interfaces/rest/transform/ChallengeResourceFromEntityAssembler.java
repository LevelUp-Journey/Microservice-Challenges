package com.levelupjourney.microservicechallenges.challenges.interfaces.rest.transform;

import com.levelupjourney.microservicechallenges.challenges.domain.model.aggregates.Challenge;
import com.levelupjourney.microservicechallenges.challenges.interfaces.rest.resource.ChallengeResource;
import com.levelupjourney.microservicechallenges.challenges.interfaces.rest.resource.StarResource;

import java.util.stream.Collectors;

public class ChallengeResourceFromEntityAssembler {

    /**
     * Convert Challenge entity to ChallengeResource with like information.
     * 
     * @param entity The challenge entity
     * @param userLiked Whether the current user has liked this challenge
     * @param likesCount Total number of likes for this challenge
     * @return ChallengeResource with complete information
     */
    public static ChallengeResource toResourceFromEntity(Challenge entity, boolean userLiked, long likesCount) {
        return new ChallengeResource(
            entity.getId().id().toString(),
            entity.getTeacherId().id().toString(),
            entity.getName(),
            entity.getDescription(),
            entity.getExperiencePoints(),
            entity.getDifficulty().name(),
            entity.getStatus().name(),
            entity.getTags(), // Already List<String>
            entity.getStars().stream()
                .map(star -> new StarResource(star.getUserId(), star.getStarredAt()))
                .collect(Collectors.toList()),
            entity.getGuides(), // List<UUID>
            entity.getMaxAttemptsBeforeGuides(),
            userLiked,
            likesCount
        );
    }

    /**
     * Convert Challenge entity to ChallengeResource without like information.
     * Used for contexts where like data is not available (e.g., bulk operations without user context).
     * 
     * @param entity The challenge entity
     * @return ChallengeResource with default like values (false, 0)
     * @deprecated Use {@link #toResourceFromEntity(Challenge, boolean, long)} instead
     */
    @Deprecated
    public static ChallengeResource toResourceFromEntity(Challenge entity) {
        return toResourceFromEntity(entity, false, 0L);
    }
}