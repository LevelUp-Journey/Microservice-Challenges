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

    // Find code versions by challenge using clean method name
    @Query("SELECT cv FROM CodeVersion cv WHERE cv.challengeId.id = :challengeId")
    List<CodeVersion> findByChallengeId(@Param("challengeId") UUID challengeId);

    // Find code version by challenge and language using clean method name
    @Query("SELECT cv FROM CodeVersion cv WHERE cv.challengeId.id = :challengeId AND cv.language = :language")
    Optional<CodeVersion> findByChallengeIdAndLanguage(@Param("challengeId") UUID challengeId, @Param("language") CodeLanguage language);

    // Find all code versions for a specific challenge and language
    @Query("SELECT cv FROM CodeVersion cv WHERE cv.challengeId.id = :challengeId AND cv.language = :language")
    List<CodeVersion> findByChallengeAndLanguage(@Param("challengeId") UUID challengeId, @Param("language") CodeLanguage language);

    // Count code versions for a challenge using clean method name
    @Query("SELECT COUNT(cv) FROM CodeVersion cv WHERE cv.challengeId.id = :challengeId")
    long countByChallengeId(@Param("challengeId") UUID challengeId);
}
