package com.levelupjourney.microservicechallenges.challenge.infrastructure.persistence.jpa.repository;

import com.levelupjourney.microservicechallenges.challenge.domain.model.aggregates.Challenge;
import com.levelupjourney.microservicechallenges.challenge.domain.model.entities.ChallengeVersion;
import com.levelupjourney.microservicechallenges.shared.domain.model.valueobjects.Language;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChallengeVersionRepository extends JpaRepository<ChallengeVersion, Long> {
    
    /**
     * Find versions by challenge
     */
    List<ChallengeVersion> findByChallenge(Challenge challenge);
    
    /**
     * Find version by challenge and language
     */
    Optional<ChallengeVersion> findByChallengeAndVersion(Challenge challenge, Language version);
    
    /**
     * Find versions by language
     */
    List<ChallengeVersion> findByVersion(Language version);
    
    /**
     * Count versions by challenge
     */
    long countByChallenge(Challenge challenge);
    
    /**
     * Find latest version by challenge
     */
    @Query("SELECT cv FROM ChallengeVersion cv WHERE cv.challenge = :challenge ORDER BY cv.id DESC")
    Optional<ChallengeVersion> findLatestVersionByChallenge(@Param("challenge") Challenge challenge);
    
    /**
     * Find all versions ordered by challenge and version
     */
    List<ChallengeVersion> findByChallengeOrderByVersion(Challenge challenge);
    
    /**
     * Delete versions by challenge
     */
    void deleteByChallenge(Challenge challenge);
    
    /**
     * Check if version exists for challenge and language
     */
    boolean existsByChallengeAndVersion(Challenge challenge, Language version);
}
