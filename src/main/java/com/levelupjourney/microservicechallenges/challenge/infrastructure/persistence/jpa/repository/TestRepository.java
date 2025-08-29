package com.levelupjourney.microservicechallenges.challenge.infrastructure.persistence.jpa.repository;

import com.levelupjourney.microservicechallenges.challenge.domain.model.aggregates.Test;
import com.levelupjourney.microservicechallenges.challenge.domain.model.entities.ChallengeVersion;
import com.levelupjourney.microservicechallenges.shared.domain.model.valueobjects.TestId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TestRepository extends JpaRepository<Test, TestId> {
    
    /**
     * Find tests by challenge version
     */
    List<Test> findByChallengeVersion(ChallengeVersion challengeVersion);
    
    /**
     * Find tests by challenge version ID
     */
    @Query("SELECT t FROM Test t WHERE t.challengeVersion.id = :challengeVersionId")
    List<Test> findByChallengeVersionId(@Param("challengeVersionId") Long challengeVersionId);
    
    /**
     * Count tests by challenge version
     */
    long countByChallengeVersion(ChallengeVersion challengeVersion);
    
    /**
     * Find tests by title containing (case insensitive)
     */
    List<Test> findByTitleContainingIgnoreCase(String title);
    
    /**
     * Find tests ordered by title
     */
    List<Test> findByChallengeVersionOrderByTitle(ChallengeVersion challengeVersion);
    
    /**
     * Delete tests by challenge version
     */
    void deleteByChallengeVersion(ChallengeVersion challengeVersion);
}
