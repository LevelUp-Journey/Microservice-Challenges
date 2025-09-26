package com.levelupjourney.microservicechallenges.challenges.interfaces.rest.transform;

import com.levelupjourney.microservicechallenges.challenges.domain.model.aggregates.CodeVersion;
import com.levelupjourney.microservicechallenges.challenges.interfaces.rest.resource.CodeVersionResource;

public class CodeVersionResourceFromEntityAssembler {

    public static CodeVersionResource toResourceFromEntity(CodeVersion entity) {
        return new CodeVersionResource(
            entity.getId().toString(),
            entity.getChallengeId().value().toString(),
            entity.getLanguage().name(),
            entity.getInitialCode()
        );
    }
}