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


    public CodeVersionToBeSubmittedResource fetchCodeVersionDetailsForSubmittingByCodeVersionId(String codeVersionId) {
        var details = challengesContextFacade.getChallengeDetailsToBeSubmitted(codeVersionId);
        if (details.isEmpty()) {
            throw new IllegalArgumentException("Code version details not found for ID: " + codeVersionId);
        }

        return details.get();
    }

}
