package com.levelupjourney.microservicechallenges.challenges.infrastructure.persistence.jpa.repositories;

import com.levelupjourney.microservicechallenges.challenges.domain.model.aggregates.Challenge;
import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.ChallengeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChallengeRepository extends JpaRepository<Challenge, UUID> {

    // Find challenge by ChallengeId (for UpdateChallengeCommand, PublishChallengeCommand, StartChallengeCommand)
    Optional<Challenge> findById_Value(UUID challengeId);

    // Find challenges by teacher (useful for teacher-specific operations)
    List<Challenge> findByTeacherId_Value(UUID teacherId);

    // Find challenges by status
    List<Challenge> findByStatus(ChallengeStatus status);

    // Find published challenges
    @Query("SELECT c FROM Challenge c WHERE c.status = com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.ChallengeStatus.PUBLISHED")
    List<Challenge> findPublishedChallenges();

    // Check if challenge exists by id
    boolean existsById_Value(UUID challengeId);
}
