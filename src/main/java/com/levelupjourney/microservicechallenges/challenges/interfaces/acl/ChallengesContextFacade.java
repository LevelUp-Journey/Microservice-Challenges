package com.levelupjourney.microservicechallenges.challenges.interfaces.acl;

import com.levelupjourney.microservicechallenges.shared.interfaces.rest.resources.CodeVersionToBeSubmittedResource;

import java.util.Optional;

public interface ChallengesContextFacade {

    Optional<CodeVersionToBeSubmittedResource> getChallengeDetailsToBeSubmitted(String codeVersionId);

}
