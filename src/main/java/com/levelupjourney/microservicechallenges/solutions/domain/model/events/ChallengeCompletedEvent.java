package com.levelupjourney.microservicechallenges.solutions.domain.model.events;

import lombok.Getter;

import java.time.LocalDateTime;

/**
 * Domain event triggered when a student completes a challenge.
 * This event carries information about the completion result including score earned.
 * Published to external systems (Profile Service) via Kafka for points accumulation and ranking.
 * <p>
 * Time Tracking:
 * - executionTimeMs: Code execution time (how long tests took to run)
 * - solutionTimeSeconds: Time taken to solve the challenge (from start to completion)
 * <p>
 * Scoring Information:
 * - scoreMultiplier: Percentage multiplier applied based on time performance (100%, 80%, 60%, 40%, 20%)
 * - timePenaltyApplied: Whether a time penalty was applied to the score
 * - scoringReason: Human-readable explanation of why this score was awarded
 * <p>
 * Completion Status:
 * - alreadyCompleted: Indicates if the student had already completed this challenge before (true = already completed, false = first time completion)
 *   This flag prevents the Profile Service from awarding duplicate points for challenges already completed successfully
 */
@Getter
public class ChallengeCompletedEvent {

    private final String studentId;
    private final String challengeId;
    private final String solutionId;
    private final Integer experiencePointsEarned;
    private final Integer totalExperiencePoints;
    private final Integer passedTests;
    private final Integer totalTests;
    private final Boolean allTestsPassed;
    private final Long executionTimeMs;
    private final Long solutionTimeSeconds;
    private final Integer scoreMultiplier;
    private final Boolean timePenaltyApplied;
    private final String scoringReason;
    private final Boolean alreadyCompleted;
    private final LocalDateTime completedAt;
    private final LocalDateTime occurredOn;

    public ChallengeCompletedEvent(
            String studentId,
            String challengeId,
            String solutionId,
            Integer experiencePointsEarned,
            Integer totalExperiencePoints,
            Integer passedTests,
            Integer totalTests,
            Boolean allTestsPassed,
            Long executionTimeMs,
            Long solutionTimeSeconds,
            Integer scoreMultiplier,
            Boolean timePenaltyApplied,
            String scoringReason,
            Boolean alreadyCompleted,
            LocalDateTime completedAt
    ) {
        this.studentId = studentId;
        this.challengeId = challengeId;
        this.solutionId = solutionId;
        this.experiencePointsEarned = experiencePointsEarned;
        this.totalExperiencePoints = totalExperiencePoints;
        this.passedTests = passedTests;
        this.totalTests = totalTests;
        this.allTestsPassed = allTestsPassed;
        this.executionTimeMs = executionTimeMs;
        this.solutionTimeSeconds = solutionTimeSeconds;
        this.scoreMultiplier = scoreMultiplier;
        this.timePenaltyApplied = timePenaltyApplied;
        this.scoringReason = scoringReason;
        this.alreadyCompleted = alreadyCompleted;
        this.completedAt = completedAt;
        this.occurredOn = LocalDateTime.now();
    }

    /**
     * Calculate success rate percentage
     */
    public double getSuccessRate() {
        if (totalTests == null || totalTests == 0) {
            return 0.0;
        }
        return (passedTests != null ? passedTests : 0) * 100.0 / totalTests;
    }

    /**
     * Check if this was a successful completion (all tests passed)
     */
    public boolean isSuccessfulCompletion() {
        return Boolean.TRUE.equals(allTestsPassed);
    }
}
