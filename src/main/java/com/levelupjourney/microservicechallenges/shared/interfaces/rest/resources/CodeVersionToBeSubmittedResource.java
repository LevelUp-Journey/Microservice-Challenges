package com.levelupjourney.microservicechallenges.shared.interfaces.rest.resources;

import java.util.List;

public record CodeVersionToBeSubmittedResource(
        String codeLanguage,
        List<CodeVersionTestForSubmittingResource> tests
) {
}
