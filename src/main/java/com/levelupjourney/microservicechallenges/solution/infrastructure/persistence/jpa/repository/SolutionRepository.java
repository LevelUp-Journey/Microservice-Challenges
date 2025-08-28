package com.levelupjourney.microservicechallenges.solution.infrastructure.persistence.jpa.repository;

import com.levelupjourney.microservicechallenges.shared.domain.model.valueobjects.ChallengeId;
import com.levelupjourney.microservicechallenges.shared.domain.model.valueobjects.Language;
import com.levelupjourney.microservicechallenges.shared.domain.model.valueobjects.StudentId;
import com.levelupjourney.microservicechallenges.solution.domain.model.aggregates.Solution;
import com.levelupjourney.microservicechallenges.solution.domain.model.valueobjects.SolutionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SolutionRepository extends JpaRepository<Solution, SolutionId> {
    
    /**
     * Find solutions by student ID
     */
    List<Solution> findByStudentId(StudentId studentId);
    
    /**
     * Find solutions by challenge ID
     */
    List<Solution> findByChallengeId(ChallengeId challengeId);
    
    /**
     * Find solution by student ID and challenge ID
     */
    Optional<Solution> findByStudentIdAndChallengeId(StudentId studentId, ChallengeId challengeId);
    
    /**
     * Find solutions by language
     */
    List<Solution> findByLanguage(Language language);
    
    /**
     * Find solutions by student ID and language
     */
    List<Solution> findByStudentIdAndLanguage(StudentId studentId, Language language);
    
    /**
     * Find solutions by challenge ID and language
     */
    List<Solution> findByChallengeIdAndLanguage(ChallengeId challengeId, Language language);
    
    /**
     * Count solutions by student ID
     */
    long countByStudentId(StudentId studentId);
    
    /**
     * Count solutions by challenge ID
     */
    long countByChallengeId(ChallengeId challengeId);
    
    /**
     * Find latest solutions by student ID
     */
    @Query("SELECT s FROM Solution s WHERE s.studentId = :studentId ORDER BY s.id DESC")
    List<Solution> findLatestSolutionsByStudentId(@Param("studentId") StudentId studentId);
    
    /**
     * Find solutions with passed tests count greater than specified value
     */
    @Query("SELECT s FROM Solution s WHERE SIZE(s.passedTests.testIds) > :minPassedTests")
    List<Solution> findSolutionsWithPassedTestsGreaterThan(@Param("minPassedTests") int minPassedTests);
    
    /**
     * Check if student has solution for challenge
     */
    boolean existsByStudentIdAndChallengeId(StudentId studentId, ChallengeId challengeId);
}
