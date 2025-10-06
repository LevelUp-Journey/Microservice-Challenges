package com.levelupjourney.microservicechallenges.challenges.domain.services;

import com.levelupjourney.microservicechallenges.challenges.domain.model.aggregates.CodeVersionTest;
import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.CodeVersionId;
import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.CodeVersionTestId;

import java.util.List;
import java.util.Optional;

public interface CodeVersionTestQueryService {
    Optional<CodeVersionTest> getCodeVersionTestById(CodeVersionTestId testId);
    List<CodeVersionTest> getCodeVersionTestsByCodeVersionId(CodeVersionId codeVersionId);
    long countTestsByCodeVersionId(CodeVersionId codeVersionId);
}