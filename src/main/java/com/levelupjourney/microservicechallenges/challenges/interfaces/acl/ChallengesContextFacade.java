package com.levelupjourney.microservicechallenges.challenges.interfaces.acl;

import com.levelupjourney.microservicechallenges.shared.interfaces.rest.resources.CodeVersionToBeSubmittedResource;

import java.util.Optional;

public interface ChallengesContextFacade {

    /**
     * Gets code version details required for solution submission.
     * Returns language and all tests associated with the code version.
     */
    Optional<CodeVersionToBeSubmittedResource> getCodeVersionForSubmission(String codeVersionId);

}
