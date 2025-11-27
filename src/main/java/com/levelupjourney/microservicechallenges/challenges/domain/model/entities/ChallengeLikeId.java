package com.levelupjourney.microservicechallenges.challenges.domain.model.entities;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

/**
 * Composite key for ChallengeLike entity.
 * 
 * <p>This ensures that a user can only like a challenge once.</p>
 */
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ChallengeLikeId implements Serializable {
    private UUID challengeId;
    private UUID userId;
}
