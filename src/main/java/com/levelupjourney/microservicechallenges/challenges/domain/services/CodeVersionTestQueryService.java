package com.levelupjourney.microservicechallenges.challenges.domain.services;

import com.levelupjourney.microservicechallenges.challenges.domain.model.aggregates.CodeVersionTest;
import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.CodeVersionId;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CodeVersionTestQueryService {
    Optional<CodeVersionTest> getCodeVersionTestById(UUID testId);
    List<CodeVersionTest> getCodeVersionTestsByCodeVersionId(CodeVersionId codeVersionId);
    long countTestsByCodeVersionId(CodeVersionId codeVersionId);
}