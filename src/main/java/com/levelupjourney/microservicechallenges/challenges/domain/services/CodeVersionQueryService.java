package com.levelupjourney.microservicechallenges.challenges.domain.services;

import com.levelupjourney.microservicechallenges.challenges.domain.model.aggregates.CodeVersion;
import com.levelupjourney.microservicechallenges.challenges.domain.model.queries.GetCodeVersionByIdQuery;
import com.levelupjourney.microservicechallenges.challenges.domain.model.queries.GetCodeVersionsByChallengeIdQuery;
import com.levelupjourney.microservicechallenges.challenges.domain.model.queries.GetCodeVersionsByChallengeIdsQuery;

import java.util.List;
import java.util.Optional;

public interface CodeVersionQueryService {
    Optional<CodeVersion> handle(GetCodeVersionByIdQuery query);
    List<CodeVersion> handle(GetCodeVersionsByChallengeIdQuery query);
    List<CodeVersion> handle(GetCodeVersionsByChallengeIdsQuery query);
}
