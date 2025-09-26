package com.levelupjourney.microservicechallenges.challenges.infrastructure.persistence.jpa.repositories;

import com.levelupjourney.microservicechallenges.challenges.domain.model.aggregates.CodeVersion;
import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.CodeLanguage;
import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.CodeVersionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CodeVersionRepository extends JpaRepository<CodeVersion, CodeVersionId> {

    // Find code versions by challenge (for AddCodeVersionCommand)
    List<CodeVersion> findByChallengeId_Value(UUID challengeId);

    // Find code version by challenge and language (to check if version already exists)
    Optional<CodeVersion> findByChallengeId_ValueAndLanguage(UUID challengeId, CodeLanguage language);

    // Find all code versions for a specific challenge and language
    @Query("SELECT cv FROM CodeVersion cv WHERE cv.challengeId.value = :challengeId AND cv.language = :language")
    List<CodeVersion> findByChallengeAndLanguage(@Param("challengeId") UUID challengeId, @Param("language") CodeLanguage language);

    // Count code versions for a challenge
    long countByChallengeId_Value(UUID challengeId);
}
