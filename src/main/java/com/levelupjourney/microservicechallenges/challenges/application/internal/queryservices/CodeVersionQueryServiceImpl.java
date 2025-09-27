package com.levelupjourney.microservicechallenges.challenges.application.internal.queryservices;

import com.levelupjourney.microservicechallenges.challenges.domain.model.aggregates.CodeVersion;
import com.levelupjourney.microservicechallenges.challenges.domain.model.queries.GetCodeVersionByIdQuery;
import com.levelupjourney.microservicechallenges.challenges.domain.model.queries.GetCodeVersionsByChallengeIdQuery;
import com.levelupjourney.microservicechallenges.challenges.domain.services.CodeVersionQueryService;
import com.levelupjourney.microservicechallenges.challenges.infrastructure.persistence.jpa.repositories.CodeVersionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CodeVersionQueryServiceImpl implements CodeVersionQueryService {

    private final CodeVersionRepository codeVersionRepository;

    public CodeVersionQueryServiceImpl(CodeVersionRepository codeVersionRepository) {
        this.codeVersionRepository = codeVersionRepository;
    }

    @Override
    public Optional<CodeVersion> handle(GetCodeVersionByIdQuery query) {
        return codeVersionRepository.findById(query.codeVersionId());
    }

    @Override
    public List<CodeVersion> handle(GetCodeVersionsByChallengeIdQuery query) {
        return codeVersionRepository.findByChallengeId(query.challengeId().value());
    }
}
