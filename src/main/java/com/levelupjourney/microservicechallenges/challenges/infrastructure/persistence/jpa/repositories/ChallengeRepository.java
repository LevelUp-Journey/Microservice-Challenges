package com.levelupjourney.microservicechallenges.challenges.infrastructure.persistence.jpa.repositories;

import com.levelupjourney.microservicechallenges.challenges.domain.model.aggregates.Challenge;
import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.ChallengeId;
import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.ChallengeStatus;
import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.Difficulty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ChallengeRepository extends JpaRepository<Challenge, ChallengeId> {

    // Find challenges by teacher ID using clean method name
    @Query("SELECT c FROM Challenge c WHERE c.teacherId.id = :teacherId")
    List<Challenge> findByTeacherId(@Param("teacherId") UUID teacherId);

    // Find published challenges by teacher ID
    @Query("SELECT c FROM Challenge c WHERE c.teacherId.id = :teacherId AND c.status = com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.ChallengeStatus.PUBLISHED")
    List<Challenge> findPublishedChallengesByTeacherId(@Param("teacherId") UUID teacherId);

    // Find challenges by status
    List<Challenge> findByStatus(ChallengeStatus status);

    // Find published challenges
    @Query("SELECT c FROM Challenge c WHERE c.status = com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.ChallengeStatus.PUBLISHED")
    List<Challenge> findPublishedChallenges();

    // Search published challenges by name (case-insensitive, partial match)
    @Query("SELECT c FROM Challenge c WHERE c.status = com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.ChallengeStatus.PUBLISHED AND LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Challenge> searchPublishedChallengesByName(@Param("name") String name);

    // Search published challenges by name and difficulty
    @Query("SELECT c FROM Challenge c WHERE c.status = com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.ChallengeStatus.PUBLISHED AND LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%')) AND c.difficulty = :difficulty")
    List<Challenge> searchPublishedChallengesByNameAndDifficulty(@Param("name") String name, @Param("difficulty") Difficulty difficulty);

    // Search published challenges by difficulty only
    @Query("SELECT c FROM Challenge c WHERE c.status = com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.ChallengeStatus.PUBLISHED AND c.difficulty = :difficulty")
    List<Challenge> searchPublishedChallengesByDifficulty(@Param("difficulty") Difficulty difficulty);
}

