package com.levelupjourney.microservicechallenges.challenges.application.internal.queryservices;

import com.levelupjourney.microservicechallenges.challenges.domain.model.aggregates.CodeVersionTest;
import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.CodeVersionId;
import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.CodeVersionTestId;
import com.levelupjourney.microservicechallenges.challenges.domain.services.CodeVersionTestQueryService;
import com.levelupjourney.microservicechallenges.challenges.infrastructure.persistence.jpa.repositories.CodeVersionTestRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CodeVersionTestQueryServiceImpl implements CodeVersionTestQueryService {

    private final CodeVersionTestRepository codeVersionTestRepository;

    public CodeVersionTestQueryServiceImpl(CodeVersionTestRepository codeVersionTestRepository) {
        this.codeVersionTestRepository = codeVersionTestRepository;
    }

    @Override
    public Optional<CodeVersionTest> getCodeVersionTestById(CodeVersionTestId testId) {
        return codeVersionTestRepository.findById(testId);
    }

    @Override
    public List<CodeVersionTest> getCodeVersionTestsByCodeVersionId(CodeVersionId codeVersionId) {
        return codeVersionTestRepository.findByCodeVersionIdOrderByCreatedAt(codeVersionId.value());
    }

    @Override
    public long countTestsByCodeVersionId(CodeVersionId codeVersionId) {
        return codeVersionTestRepository.countByCodeVersionId(codeVersionId.value());
    }
}
