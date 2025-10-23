package com.levelupjourney.microservicechallenges.challenges.interfaces.rest.transform;

import com.levelupjourney.microservicechallenges.challenges.domain.model.aggregates.CodeVersion;
import com.levelupjourney.microservicechallenges.challenges.interfaces.rest.resource.CodeVersionResource;

public class CodeVersionResourceFromEntityAssembler {

    public static CodeVersionResource toResourceFromEntity(CodeVersion entity) {
        return new CodeVersionResource(
            entity.getId().id().toString(),
            entity.getChallengeId().id().toString(),
            entity.getLanguage().name(),
            entity.getInitialCode(),
            entity.getFunctionName()
        );
    }
}