package com.levelupjourney.microservicechallenges.solutions.infrastructure.persistence.jpa.repositories;

import com.levelupjourney.microservicechallenges.solutions.domain.model.aggregates.Solution;
import com.levelupjourney.microservicechallenges.solutions.domain.model.valueobjects.SolutionId;
import com.levelupjourney.microservicechallenges.solutions.domain.model.valueobjects.SolutionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SolutionRepository extends JpaRepository<Solution, SolutionId> {

    // Find solution by student and code version using clean method names
    @Query("SELECT s FROM Solution s WHERE s.studentId.value = :studentId AND s.codeVersionId.value = :codeVersionId")
    Optional<Solution> findByStudentIdAndCodeVersionId(@Param("studentId") UUID studentId, @Param("codeVersionId") UUID codeVersionId);

    // Find all solutions by student ID using clean method name
    @Query("SELECT s FROM Solution s WHERE s.studentId.value = :studentId")
    List<Solution> findByStudentId(@Param("studentId") UUID studentId);

    // Find all solutions by challenge ID using clean method name
    @Query("SELECT s FROM Solution s WHERE s.challengeId.value = :challengeId")
    List<Solution> findByChallengeId(@Param("challengeId") UUID challengeId);

    // Find solutions by student and challenge using clean method names
    @Query("SELECT s FROM Solution s WHERE s.studentId.value = :studentId AND s.challengeId.value = :challengeId")
    List<Solution> findByStudentIdAndChallengeId(@Param("studentId") UUID studentId, @Param("challengeId") UUID challengeId);

    // Find solutions by status
    List<Solution> findByDetails_Status(SolutionStatus status);

    // Find solutions by student and status using clean method name
    @Query("SELECT s FROM Solution s WHERE s.studentId.value = :studentId AND s.details.status = :status")
    List<Solution> findByStudentIdAndStatus(@Param("studentId") UUID studentId, @Param("status") SolutionStatus status);

    // Check if solution exists for student and code version using clean method names
    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM Solution s WHERE s.studentId.value = :studentId AND s.codeVersionId.value = :codeVersionId")
    boolean existsByStudentIdAndCodeVersionId(@Param("studentId") UUID studentId, @Param("codeVersionId") UUID codeVersionId);

    // Count solutions by student using clean method name
    @Query("SELECT COUNT(s) FROM Solution s WHERE s.studentId.value = :studentId")
    long countByStudentId(@Param("studentId") UUID studentId);

    // Count solutions by challenge using clean method name
    @Query("SELECT COUNT(s) FROM Solution s WHERE s.challengeId.value = :challengeId")
    long countByChallengeId(@Param("challengeId") UUID challengeId);

    // Find successful solutions by challenge (for analytics)
    @Query("SELECT s FROM Solution s WHERE s.challengeId.value = :challengeId AND s.details.status = 'SUCCESS'")
    List<Solution> findSuccessfulSolutionsByChallengeId(@Param("challengeId") UUID challengeId);

    // Find solutions with most attempts by challenge
    @Query("SELECT s FROM Solution s WHERE s.challengeId.value = :challengeId ORDER BY s.details.attempts DESC")
    List<Solution> findSolutionsByChallengeIdOrderByAttemptsDesc(@Param("challengeId") UUID challengeId);
}
