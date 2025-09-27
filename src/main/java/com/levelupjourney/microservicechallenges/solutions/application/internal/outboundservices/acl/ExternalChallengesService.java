package com.levelupjourney.microservicechallenges.solutions.application.internal.outboundservices.acl;

import com.levelupjourney.microservicechallenges.challenges.interfaces.acl.ChallengesContextFacade;
import com.levelupjourney.microservicechallenges.shared.interfaces.rest.resources.CodeVersionToBeSubmittedResource;
import org.springframework.stereotype.Service;

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
}
