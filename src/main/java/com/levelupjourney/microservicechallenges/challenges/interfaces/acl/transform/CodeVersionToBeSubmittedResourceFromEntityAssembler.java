package com.levelupjourney.microservicechallenges.challenges.interfaces.acl.transform;

import com.levelupjourney.microservicechallenges.challenges.domain.model.aggregates.CodeVersion;
import com.levelupjourney.microservicechallenges.challenges.domain.model.aggregates.CodeVersionTest;
import com.levelupjourney.microservicechallenges.shared.interfaces.rest.resources.CodeVersionTestForSubmittingResource;
import com.levelupjourney.microservicechallenges.shared.interfaces.rest.resources.CodeVersionToBeSubmittedResource;

public class CodeVersionToBeSubmittedResourceFromEntityAssembler {

    public static CodeVersionToBeSubmittedResource toResourceFromEntity(CodeVersion codeVersion) {

        var tests = codeVersion.getTests().stream()
                .map(CodeVersionToBeSubmittedResourceFromEntityAssembler::toCodeVersionTestForSubmittingResourceFromEntity)
                .toList();

        return new CodeVersionToBeSubmittedResource(
                codeVersion.getLanguage().name().toLowerCase(), // Use language instead of ID
                tests
        );
    }

    public static CodeVersionTestForSubmittingResource toCodeVersionTestForSubmittingResourceFromEntity(CodeVersionTest codeVersionTest) {
        return new CodeVersionTestForSubmittingResource(
                codeVersionTest.getId().value().toString(), // Use test ID, not CodeVersion ID
                codeVersionTest.getInput(),
                codeVersionTest.getExpectedOutput(),
                codeVersionTest.getCustomValidationCode()
        );
    }

}
