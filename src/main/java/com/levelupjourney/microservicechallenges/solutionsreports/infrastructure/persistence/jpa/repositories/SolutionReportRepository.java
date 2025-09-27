package com.levelupjourney.microservicechallenges.solutionsreports.infrastructure.persistence.jpa.repositories;

import com.levelupjourney.microservicechallenges.solutionsreports.domain.model.aggregates.SolutionReport;
import com.levelupjourney.microservicechallenges.solutionsreports.domain.model.valueobjects.SolutionReportId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SolutionReportRepository extends JpaRepository<SolutionReport, SolutionReportId> {

    // Find solution report by solution ID using clean method name
    @Query("SELECT sr FROM SolutionReport sr WHERE sr.solutionId.value = :solutionId")
    Optional<SolutionReport> findBySolutionId(@Param("solutionId") UUID solutionId);

    // Find all solution reports by student ID using clean method name
    @Query("SELECT sr FROM SolutionReport sr WHERE sr.studentId.value = :studentId")
    List<SolutionReport> findByStudentId(@Param("studentId") UUID studentId);

    // Find solution reports by student and solution using clean method names
    @Query("SELECT sr FROM SolutionReport sr WHERE sr.studentId.value = :studentId AND sr.solutionId.value = :solutionId")
    Optional<SolutionReport> findByStudentIdAndSolutionId(@Param("studentId") UUID studentId, @Param("solutionId") UUID solutionId);

    // Check if solution report exists for a solution using clean method name
    @Query("SELECT CASE WHEN COUNT(sr) > 0 THEN true ELSE false END FROM SolutionReport sr WHERE sr.solutionId.value = :solutionId")
    boolean existsBySolutionId(@Param("solutionId") UUID solutionId);

    // Count reports by student using clean method name
    @Query("SELECT COUNT(sr) FROM SolutionReport sr WHERE sr.studentId.value = :studentId")
    long countByStudentId(@Param("studentId") UUID studentId);

    // Find reports with best performance by student (least time taken)
    @Query("SELECT sr FROM SolutionReport sr WHERE sr.studentId.value = :studentId ORDER BY sr.timeTaken ASC")
    List<SolutionReport> findReportsByStudentIdOrderByTimeAsc(@Param("studentId") UUID studentId);

    // Find reports with least memory usage by student
    @Query("SELECT sr FROM SolutionReport sr WHERE sr.studentId.value = :studentId ORDER BY sr.memoryUsed ASC")
    List<SolutionReport> findReportsByStudentIdOrderByMemoryAsc(@Param("studentId") UUID studentId);
    
    // Delete solution report by solution ID using clean method name
    @Modifying
    @Query("DELETE FROM SolutionReport sr WHERE sr.solutionId.value = :solutionId")
    void deleteBySolutionId(@Param("solutionId") UUID solutionId);
    
    // Delete all solution reports by student ID using clean method name
    @Modifying
    @Query("DELETE FROM SolutionReport sr WHERE sr.studentId.value = :studentId")
    void deleteByStudentId(@Param("studentId") UUID studentId);
}
