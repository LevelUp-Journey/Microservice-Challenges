package com.levelupjourney.microservicechallenges.challenges.interfaces.acl;

import com.levelupjourney.microservicechallenges.shared.interfaces.rest.resources.ChallengeForScoringResource;
import com.levelupjourney.microservicechallenges.shared.interfaces.rest.resources.CodeVersionToBeSubmittedResource;

import java.util.Optional;

public interface ChallengesContextFacade {

    /**
     * Gets code version details required for solution submission.
     * Returns language and all tests associated with the code version.
     */
    Optional<CodeVersionToBeSubmittedResource> getCodeVersionForSubmission(String codeVersionId);

    /**
     * Gets challenge information needed for score calculation.
     * Returns the experience points associated with a challenge.
     *
     * @param challengeId The challenge identifier
     * @return Challenge information with experience points
     */
    Optional<ChallengeForScoringResource> getChallengeForScoring(String challengeId);

}
