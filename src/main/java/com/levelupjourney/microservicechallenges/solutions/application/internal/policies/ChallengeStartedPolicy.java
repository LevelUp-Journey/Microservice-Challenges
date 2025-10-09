package com.levelupjourney.microservicechallenges.solutions.application.internal.policies;

import com.levelupjourney.microservicechallenges.challenges.domain.model.events.ChallengeStartedEvent;
import com.levelupjourney.microservicechallenges.solutions.interfaces.acl.SolutionsAcl;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Business policy that handles ChallengeStartedEvent.
 * Creates a default solution when a student starts a challenge.
 */
@Component
public class ChallengeStartedPolicy {

    private final SolutionsAcl solutionsAcl;

    public ChallengeStartedPolicy(SolutionsAcl solutionsAcl) {
        this.solutionsAcl = solutionsAcl;
    }

    /**
     * Handles the ChallengeStartedEvent by creating a default solution.
     * This ensures that challenges bounded context doesn't directly depend on solutions.
     * Passes primitive types (String) to ACL to maintain decoupling.
     */
    @EventListener
    public void handleChallengeStarted(ChallengeStartedEvent event) {
        // Pass primitive types (String) to ACL instead of value objects
        solutionsAcl.createDefaultSolution(
            event.studentId().id().toString(),
            event.challengeId().id().toString(),
            event.codeVersionId().id().toString(),
            event.defaultCode()
        );
    }
}