package com.levelupjourney.microservicechallenges.challenge.infrastructure.persistence.jpa.repository;

import com.levelupjourney.microservicechallenges.challenge.domain.model.aggregates.Challenge;
import com.levelupjourney.microservicechallenges.challenge.domain.model.valueobjects.ChallengeState;
import com.levelupjourney.microservicechallenges.challenge.domain.model.valueobjects.TeacherId;
import com.levelupjourney.microservicechallenges.shared.domain.model.valueobjects.ChallengeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChallengeRepository extends JpaRepository<Challenge, ChallengeId> {
    
    /**
     * Find challenges by teacher ID
     */
    List<Challenge> findByTeacherId(TeacherId teacherId);
    
    /**
     * Find challenges by state
     */
    List<Challenge> findByState(ChallengeState state);
    
    /**
     * Find challenges by teacher ID and state
     */
    List<Challenge> findByTeacherIdAndState(TeacherId teacherId, ChallengeState state);
    
    /**
     * Find challenges by title containing (case insensitive)
     */
    List<Challenge> findByTitleContainingIgnoreCase(String title);
    
    /**
     * Count challenges by teacher ID
     */
    long countByTeacherId(TeacherId teacherId);
    
    /**
     * Find published challenges
     */
    @Query("SELECT c FROM Challenge c WHERE c.state = 'PUBLISHED'")
    List<Challenge> findPublishedChallenges();
    
    /**
     * Find challenges by teacher ID ordered by creation date
     */
    @Query("SELECT c FROM Challenge c WHERE c.teacherId = :teacherId ORDER BY c.id DESC")
    List<Challenge> findByTeacherIdOrderedByCreation(@Param("teacherId") TeacherId teacherId);
}
