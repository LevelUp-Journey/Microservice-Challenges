package com.levelupjourney.microservicechallenges.solutions.application.internal.outboundservices.acl;

import com.levelupjourney.microservicechallenges.challenges.interfaces.acl.ChallengesContextFacade;
import com.levelupjourney.microservicechallenges.shared.interfaces.rest.resources.ChallengeForScoringResource;
import com.levelupjourney.microservicechallenges.shared.interfaces.rest.resources.CodeVersionToBeSubmittedResource;
import org.springframework.stereotype.Service;

/**
 * External Challenges Service
 * ACL implementation for accessing Challenge context from Solutions context
 */
@Service
public class ExternalChallengesService {
    private final ChallengesContextFacade challengesContextFacade;

    public ExternalChallengesService(ChallengesContextFacade challengesContextFacade) {
        this.challengesContextFacade = challengesContextFacade;
    }

    /**
     * Get code version details required for solution submission.
     * Includes code language and all associated tests.
     */
    public CodeVersionToBeSubmittedResource getCodeVersionDetailsForSubmission(String codeVersionId) {
        var details = challengesContextFacade.getCodeVersionForSubmission(codeVersionId);
        if (details.isEmpty()) {
            throw new IllegalArgumentException("CodeVersion not found for submission: " + codeVersionId);
        }
        return details.get();
    }

    /**
     * Get challenge information needed for score calculation.
     * Returns the experience points that can be earned from completing this challenge.
     *
     * @param challengeId The challenge identifier
     * @return Challenge information with experience points
     */
    public ChallengeForScoringResource getChallengeForScoring(String challengeId) {
        var challenge = challengesContextFacade.getChallengeForScoring(challengeId);
        if (challenge.isEmpty()) {
            throw new IllegalArgumentException("Challenge not found for scoring: " + challengeId);
        }
        return challenge.get();
    }
}
