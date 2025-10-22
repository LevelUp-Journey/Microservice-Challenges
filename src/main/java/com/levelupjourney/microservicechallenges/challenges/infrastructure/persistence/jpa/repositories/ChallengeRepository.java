package com.levelupjourney.microservicechallenges.challenges.infrastructure.persistence.jpa.repositories;

import com.levelupjourney.microservicechallenges.challenges.domain.model.aggregates.Challenge;
import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.ChallengeId;
import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.ChallengeStatus;
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
}
