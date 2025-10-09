package com.levelupjourney.microservicechallenges.challenges.infrastructure.persistence.jpa.repositories;

import com.levelupjourney.microservicechallenges.challenges.domain.model.aggregates.CodeVersionTest;
import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.CodeVersionTestId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CodeVersionTestRepository extends JpaRepository<CodeVersionTest, CodeVersionTestId> {

    // Find all tests for a specific code version using clean method name
    @Query("SELECT cvt FROM CodeVersionTest cvt WHERE cvt.codeVersionId.id = :codeVersionId")
    List<CodeVersionTest> findByCodeVersionId(@Param("codeVersionId") UUID codeVersionId);

    // Find tests by code version with ordering using clean method name
    @Query("SELECT cvt FROM CodeVersionTest cvt WHERE cvt.codeVersionId.id = :codeVersionId ORDER BY cvt.createdAt")
    List<CodeVersionTest> findByCodeVersionIdOrderByCreatedAt(@Param("codeVersionId") UUID codeVersionId);

    // Count tests for a code version using clean method name
    @Query("SELECT COUNT(cvt) FROM CodeVersionTest cvt WHERE cvt.codeVersionId.id = :codeVersionId")
    long countByCodeVersionId(@Param("codeVersionId") UUID codeVersionId);

    // Delete all tests for a code version using clean method name
    @Modifying
    @Query("DELETE FROM CodeVersionTest cvt WHERE cvt.codeVersionId.id = :codeVersionId")
    void deleteByCodeVersionId(@Param("codeVersionId") UUID codeVersionId);
}
