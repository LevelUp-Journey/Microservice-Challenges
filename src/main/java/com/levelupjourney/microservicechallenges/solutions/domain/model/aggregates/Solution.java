package com.levelupjourney.microservicechallenges.solutions.domain.model.aggregates;

import com.levelupjourney.microservicechallenges.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import com.levelupjourney.microservicechallenges.solutions.domain.model.commands.CreateSolutionCommand;
import com.levelupjourney.microservicechallenges.solutions.domain.model.valueobjects.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Getter
@NoArgsConstructor
@Entity
public class Solution extends AuditableAbstractAggregateRoot<Solution> {
    
    @EmbeddedId
    private SolutionId id;
    
    @Embedded
    @AttributeOverride(name = "id", column = @Column(name = "challenge_id"))
    private ChallengeId challengeId;
    
    @Embedded
    @AttributeOverride(name = "id", column = @Column(name = "code_version_id"))
    private CodeVersionId codeVersionId;
    
    @Embedded
    @AttributeOverride(name = "id", column = @Column(name = "student_id"))
    private StudentId studentId;

    // Solution details fields (formerly in SolutionDetails embeddable)
    private Integer attempts;

    @NotNull
    @Lob
    @Column(columnDefinition = "TEXT")
    private String code;

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastAttemptAt;

    @NotNull
    @Enumerated(EnumType.STRING)
    private SolutionStatus status;

    // Score fields (keeping as embeddable for cohesion)
    @Embedded
    private SolutionScore score;

    public Solution(CreateSolutionCommand command) {
        this.id = new SolutionId(UUID.randomUUID());
        this.challengeId = command.challengeId();
        this.codeVersionId = command.codeVersionId();
        this.studentId = command.studentId();
        this.code = command.code();
        this.attempts = 0;
        this.status = SolutionStatus.NO_TESTED;
        this.lastAttemptAt = null;
        this.score = SolutionScore.defaultScore();
    }
    
    /**
     * Update the solution's code
     * @param code The new code to save
     */
    public void updateCode(String code) {
        this.code = code;
    }

    /**
     * Update solution after submission attempt
     * Increments attempts and updates timestamp
     */
    public void recordSubmissionAttempt() {
        this.attempts++;
        this.lastAttemptAt = new Date();
        this.status = SolutionStatus.IN_PROGRESS;
    }

    /**
     * Assign score to this solution based on test results
     * @param pointsEarned Points earned from passed tests
     * @param maxPoints Maximum possible points from the challenge
     */
    public void assignScore(Integer pointsEarned, Integer maxPoints) {
        this.score = new SolutionScore(pointsEarned, maxPoints);
        
        // Update status based on score
        if (pointsEarned.equals(maxPoints) && maxPoints > 0) {
            this.status = SolutionStatus.SUCCESS;
        } else if (pointsEarned == 0) {
            this.status = SolutionStatus.FAILED;
        } else {
            this.status = SolutionStatus.IN_PROGRESS;
        }
    }
}
