package com.levelupjourney.microservicechallenges.solutions.interfaces.rest.transform;

import com.levelupjourney.microservicechallenges.solutions.domain.model.aggregates.Solution;
import com.levelupjourney.microservicechallenges.solutions.interfaces.rest.resource.SolutionResource;

import java.text.SimpleDateFormat;

public class SolutionResourceFromEntityAssembler {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

    public static SolutionResource toResourceFromEntity(Solution entity) {
        return new SolutionResource(
            entity.getId().id().toString(),
            entity.getChallengeId().id().toString(),
            entity.getCodeVersionId().id().toString(),
            entity.getStudentId().id().toString(),
            entity.getDetails().getAttempts(),
            entity.getDetails().getCode(),
            entity.getDetails().getLastAttemptAt() != null ? 
                dateFormat.format(entity.getDetails().getLastAttemptAt()) : null,
            entity.getDetails().getStatus().name()
        );
    }
}