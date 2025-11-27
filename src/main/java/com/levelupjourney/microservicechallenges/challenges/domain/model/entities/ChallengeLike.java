package com.levelupjourney.microservicechallenges.challenges.domain.model.entities;

import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.ChallengeId;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * ChallengeLike entity represents a user's like on a challenge.
 * 
 * <p>This entity follows DDD principles as a domain entity within the Challenge aggregate.
 * It tracks which users have liked which challenges and when.</p>
 * 
 * <h3>Business Rules:</h3>
 * <ul>
 *   <li>A user can like a challenge only once</li>
 *   <li>Composite key of (challengeId, userId) ensures uniqueness</li>
 *   <li>Likes are timestamped for analytics</li>
 * </ul>
 */
@Getter
@NoArgsConstructor
@Entity
@Table(name = "challenge_likes")
@IdClass(ChallengeLikeId.class)
public class ChallengeLike {

    @Id
    @Column(name = "challenge_id", nullable = false)
    private UUID challengeId;

    @Id
    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "liked_at", nullable = false)
    private LocalDateTime likedAt;

    /**
     * Constructor to create a new like.
     * 
     * @param challengeId The challenge being liked
     * @param userId The user who is liking the challenge
     */
    public ChallengeLike(ChallengeId challengeId, UUID userId) {
        this.challengeId = challengeId.id();
        this.userId = userId;
        this.likedAt = LocalDateTime.now();
    }

    /**
     * Gets the challenge ID as a value object.
     * 
     * @return ChallengeId value object
     */
    public ChallengeId getChallengeIdVO() {
        return new ChallengeId(this.challengeId);
    }
}
