package com.levelupjourney.microservicechallenges.challenges.infrastructure.persistence.jpa.repositories;

import com.levelupjourney.microservicechallenges.challenges.domain.model.entities.ChallengeLike;
import com.levelupjourney.microservicechallenges.challenges.domain.model.entities.ChallengeLikeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository for ChallengeLike entity operations.
 */
@Repository
public interface ChallengeLikeRepository extends JpaRepository<ChallengeLike, ChallengeLikeId> {

    /**
     * Count total likes for a challenge.
     * 
     * @param challengeId The challenge UUID
     * @return Total number of likes
     */
    @Query("SELECT COUNT(cl) FROM ChallengeLike cl WHERE cl.challengeId = :challengeId")
    long countByChallengeId(@Param("challengeId") UUID challengeId);

    /**
     * Check if a user has liked a challenge.
     * 
     * @param challengeId The challenge UUID
     * @param userId The user UUID
     * @return true if the user has liked the challenge
     */
    @Query("SELECT COUNT(cl) > 0 FROM ChallengeLike cl WHERE cl.challengeId = :challengeId AND cl.userId = :userId")
    boolean existsByChallengeIdAndUserId(@Param("challengeId") UUID challengeId, @Param("userId") UUID userId);

    /**
     * Get likes count for multiple challenges (batch operation).
     * Returns pairs of (challengeId, count).
     * 
     * @param challengeIds List of challenge UUIDs
     * @return List of Object arrays where [0] is challengeId and [1] is count
     */
    @Query("SELECT cl.challengeId, COUNT(cl) FROM ChallengeLike cl WHERE cl.challengeId IN :challengeIds GROUP BY cl.challengeId")
    List<Object[]> countByChallengeIdIn(@Param("challengeIds") List<UUID> challengeIds);

    /**
     * Check which challenges a user has liked (batch operation).
     * 
     * @param challengeIds List of challenge UUIDs
     * @param userId The user UUID
     * @return List of challenge UUIDs that the user has liked
     */
    @Query("SELECT cl.challengeId FROM ChallengeLike cl WHERE cl.challengeId IN :challengeIds AND cl.userId = :userId")
    List<UUID> findLikedChallengeIdsByUserIdAndChallengeIdIn(@Param("challengeIds") List<UUID> challengeIds, @Param("userId") UUID userId);

    /**
     * Delete a like by composite key.
     * 
     * @param challengeId The challenge UUID
     * @param userId The user UUID
     */
    void deleteByChallengeIdAndUserId(UUID challengeId, UUID userId);
}
