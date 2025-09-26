package com.levelupjourney.microservicechallenges.solutionsreports.infrastructure.persistence.jpa.repositories;

import com.levelupjourney.microservicechallenges.solutionsreports.domain.model.aggregates.SolutionReport;
import com.levelupjourney.microservicechallenges.solutionsreports.domain.model.valueobjects.SolutionReportId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SolutionReportRepository extends JpaRepository<SolutionReport, SolutionReportId> {

    // Find solution report by solution ID
    Optional<SolutionReport> findBySolutionId_Value(UUID solutionId);

    // Find all solution reports by student ID
    List<SolutionReport> findByStudentId_Value(UUID studentId);

    // Find solution reports by student and solution
    Optional<SolutionReport> findByStudentId_ValueAndSolutionId_Value(UUID studentId, UUID solutionId);

    // Check if solution report exists for a solution
    boolean existsBySolutionId_Value(UUID solutionId);

    // Count reports by student
    long countByStudentId_Value(UUID studentId);

    // Find reports with best performance by student (least time taken)
    @Query("SELECT sr FROM SolutionReport sr WHERE sr.studentId.value = :studentId ORDER BY sr.timeTaken ASC")
    List<SolutionReport> findReportsByStudentIdOrderByTimeAsc(@Param("studentId") UUID studentId);

    // Find reports with least memory usage by student
    @Query("SELECT sr FROM SolutionReport sr WHERE sr.studentId.value = :studentId ORDER BY sr.memoryUsed ASC")
    List<SolutionReport> findReportsByStudentIdOrderByMemoryAsc(@Param("studentId") UUID studentId);
}
