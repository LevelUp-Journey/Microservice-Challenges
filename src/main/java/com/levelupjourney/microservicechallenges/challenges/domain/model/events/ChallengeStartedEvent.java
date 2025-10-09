package com.levelupjourney.microservicechallenges.challenges.domain.model.events;

import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.ChallengeId;
import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.CodeVersionId;
import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.StudentId;

/**
 * Domain event triggered when a student starts a challenge.
 * Contains the necessary information to create a default solution.
 */
public record ChallengeStartedEvent(
    StudentId studentId,
    ChallengeId challengeId,
    CodeVersionId codeVersionId,
    String defaultCode
) {
}