package com.levelupjourney.microservicechallenges.challenges.interfaces.rest.transform;

import com.levelupjourney.microservicechallenges.challenges.domain.model.aggregates.CodeVersionTest;
import com.levelupjourney.microservicechallenges.challenges.interfaces.rest.resource.CodeVersionTestResource;

public class CodeVersionTestResourceFromEntityAssembler {

    public static CodeVersionTestResource toResourceFromEntity(CodeVersionTest entity) {
        return new CodeVersionTestResource(
            entity.getId().toString(),
            entity.getCodeVersionId().value().toString(),
            entity.getInput(),
            entity.getExpectedOutput(),
            entity.getCustomValidationCode(),
            entity.getFailureMessage()
        );
    }
}