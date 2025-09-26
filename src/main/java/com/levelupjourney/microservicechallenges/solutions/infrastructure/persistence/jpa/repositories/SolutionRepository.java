package com.levelupjourney.microservicechallenges.solutions.infrastructure.persistence.jpa.repositories;

import com.levelupjourney.microservicechallenges.solutions.domain.model.aggregates.Solution;
import com.levelupjourney.microservicechallenges.solutions.domain.model.valueobjects.SolutionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SolutionRepository extends JpaRepository<Solution, UUID> {

    // Find solution by student and code version (from GetSolutionByStudentIdAndCodeVersionIdQuery)
    Optional<Solution> findByStudentId_ValueAndCodeVersionId_Value(UUID studentId, UUID codeVersionId);

    // Find all solutions by student ID
    List<Solution> findByStudentId_Value(UUID studentId);

    // Find all solutions by challenge ID
    List<Solution> findByChallengeId_Value(UUID challengeId);

    // Find solutions by student and challenge
    List<Solution> findByStudentId_ValueAndChallengeId_Value(UUID studentId, UUID challengeId);

    // Find solutions by status
    List<Solution> findByDetails_Status(SolutionStatus status);

    // Find solutions by student and status
    List<Solution> findByStudentId_ValueAndDetails_Status(UUID studentId, SolutionStatus status);

    // Check if solution exists for student and code version
    boolean existsByStudentId_ValueAndCodeVersionId_Value(UUID studentId, UUID codeVersionId);

    // Count solutions by student
    long countByStudentId_Value(UUID studentId);

    // Count solutions by challenge
    long countByChallengeId_Value(UUID challengeId);

    // Find successful solutions by challenge (for analytics)
    @Query("SELECT s FROM Solution s WHERE s.challengeId.value = :challengeId AND s.details.status = 'SUCCESS'")
    List<Solution> findSuccessfulSolutionsByChallengeId(@Param("challengeId") UUID challengeId);

    // Find solutions with most attempts by challenge
    @Query("SELECT s FROM Solution s WHERE s.challengeId.value = :challengeId ORDER BY s.details.attempts DESC")
    List<Solution> findSolutionsByChallengeIdOrderByAttemptsDesc(@Param("challengeId") UUID challengeId);
}
