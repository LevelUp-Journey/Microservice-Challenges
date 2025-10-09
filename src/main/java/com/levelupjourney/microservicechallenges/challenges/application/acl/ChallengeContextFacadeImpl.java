package com.levelupjourney.microservicechallenges.challenges.application.acl;

import com.levelupjourney.microservicechallenges.challenges.domain.model.queries.GetCodeVersionByIdQuery;
import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.CodeVersionId;
import com.levelupjourney.microservicechallenges.challenges.domain.services.CodeVersionQueryService;
import com.levelupjourney.microservicechallenges.challenges.interfaces.acl.ChallengesContextFacade;
import com.levelupjourney.microservicechallenges.challenges.interfaces.acl.transform.CodeVersionToBeSubmittedResourceFromEntityAssembler;
import com.levelupjourney.microservicechallenges.shared.interfaces.rest.resources.CodeVersionToBeSubmittedResource;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class ChallengeContextFacadeImpl implements ChallengesContextFacade {
    private CodeVersionQueryService codeVersionQueryService;

    public ChallengeContextFacadeImpl(CodeVersionQueryService codeVersionQueryService) {
        this.codeVersionQueryService = codeVersionQueryService;
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
}

