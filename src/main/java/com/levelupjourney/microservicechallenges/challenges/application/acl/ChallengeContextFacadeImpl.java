package com.levelupjourney.microservicechallenges.challenges.application.acl;

import com.levelupjourney.microservicechallenges.challenges.domain.model.queries.GetChallengeByIdQuery;
import com.levelupjourney.microservicechallenges.challenges.domain.model.queries.GetCodeVersionByIdQuery;
import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.ChallengeId;
import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.CodeVersionId;
import com.levelupjourney.microservicechallenges.challenges.domain.services.ChallengeQueryService;
import com.levelupjourney.microservicechallenges.challenges.domain.services.CodeVersionQueryService;
import com.levelupjourney.microservicechallenges.challenges.interfaces.acl.ChallengesContextFacade;
import com.levelupjourney.microservicechallenges.challenges.interfaces.acl.transform.CodeVersionToBeSubmittedResourceFromEntityAssembler;
import com.levelupjourney.microservicechallenges.shared.interfaces.rest.resources.ChallengeForScoringResource;
import com.levelupjourney.microservicechallenges.shared.interfaces.rest.resources.CodeVersionToBeSubmittedResource;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class ChallengeContextFacadeImpl implements ChallengesContextFacade {
    private final CodeVersionQueryService codeVersionQueryService;
    private final ChallengeQueryService challengeQueryService;

    public ChallengeContextFacadeImpl(CodeVersionQueryService codeVersionQueryService,
                                     ChallengeQueryService challengeQueryService) {
        this.codeVersionQueryService = codeVersionQueryService;
        this.challengeQueryService = challengeQueryService;
    }

    @Override
    public Optional<CodeVersionToBeSubmittedResource> getCodeVersionForSubmission(String codeVersionId) {

        var getCodeVersionByIdQuery = new GetCodeVersionByIdQuery(new CodeVersionId(UUID.fromString(codeVersionId)));
        var codeVersion = codeVersionQueryService.handle(getCodeVersionByIdQuery);

        if (codeVersion.isEmpty()) {
            return Optional.empty();
        }

        var result = codeVersion.get();

        return Optional.of(CodeVersionToBeSubmittedResourceFromEntityAssembler.toResourceFromEntity(result));
    }

    @Override
    public Optional<ChallengeForScoringResource> getChallengeForScoring(String challengeId) {
        var getChallengeByIdQuery = new GetChallengeByIdQuery(new ChallengeId(UUID.fromString(challengeId)));
        var challenge = challengeQueryService.handle(getChallengeByIdQuery);

        if (challenge.isEmpty()) {
            return Optional.empty();
        }

        var result = challenge.get();

        return Optional.of(new ChallengeForScoringResource(
            result.getId().id().toString(),
            result.getExperiencePoints()
        ));
    }
}

