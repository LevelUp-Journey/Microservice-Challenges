package com.levelupjourney.microservicechallenges.challenges.infrastructure.persistence.jpa.repositories;

import com.levelupjourney.microservicechallenges.challenges.domain.model.aggregates.CodeVersionTest;
import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.CodeVersionTestId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CodeVersionTestRepository extends JpaRepository<CodeVersionTest, CodeVersionTestId> {

    // Find all tests for a specific code version (for AddCodeVersionTestCommand)
    List<CodeVersionTest> findByCodeVersionId_Value(UUID codeVersionId);

    // Find tests by code version with ordering (useful for displaying tests in order)
    @Query("SELECT cvt FROM CodeVersionTest cvt WHERE cvt.codeVersionId.value = :codeVersionId ORDER BY cvt.createdAt")
    List<CodeVersionTest> findByCodeVersionIdOrderByCreatedAt(@Param("codeVersionId") UUID codeVersionId);

    // Count tests for a code version
    long countByCodeVersionId_Value(UUID codeVersionId);

    // Delete all tests for a code version (useful for cleanup operations)
    void deleteByCodeVersionId_Value(UUID codeVersionId);
}
