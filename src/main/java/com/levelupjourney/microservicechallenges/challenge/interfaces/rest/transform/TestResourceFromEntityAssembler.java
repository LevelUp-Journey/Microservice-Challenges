package com.levelupjourney.microservicechallenges.challenge.interfaces.rest.transform;

import com.levelupjourney.microservicechallenges.challenge.domain.model.aggregates.Test;
import com.levelupjourney.microservicechallenges.challenge.interfaces.rest.resources.TestResource;

/**
 * Assembler class to convert Test entities to REST resources
 */
public class TestResourceFromEntityAssembler {

    public static TestResource toResourceFromEntity(Test test) {
        return new TestResource(
                test.getId().id().toString(),
                test.getTitle(),
                test.getHint(),
                test.getOnErrorHint(),
                test.getTestCode(),
                test.getInput(),
                test.getExpectedOutput()
        );
    }
}
