package com.levelupjourney.microservicechallenges.solutions.domain.model.aggregates;

import com.levelupjourney.microservicechallenges.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import com.levelupjourney.microservicechallenges.solutions.domain.model.commands.CreateSolutionCommand;
import com.levelupjourney.microservicechallenges.solutions.domain.model.valueobjects.*;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
    
    @Embedded
    private SolutionDetails details;

    @Embedded
    private SolutionScore score;

    public Solution(CreateSolutionCommand command) {
        this.id = new SolutionId(UUID.randomUUID());
        this.challengeId = command.challengeId();
        this.codeVersionId = command.codeVersionId();
        this.studentId = command.studentId();
        this.details = new SolutionDetails(command.code());
        this.score = SolutionScore.defaultScore();
    }
    
    public void updateSolution(String code, String language) {
        // Update solution details with new code and language
        this.details = new SolutionDetails(code);
    }

    /**
     * Assign score to this solution based on test results
     * @param pointsEarned Points earned from passed tests
     * @param maxPoints Maximum possible points from the challenge
     */
    public void assignScore(Integer pointsEarned, Integer maxPoints) {
        this.score = new SolutionScore(pointsEarned, maxPoints);
    }
}
