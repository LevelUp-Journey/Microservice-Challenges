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
    
    @Id
    private UUID id;
    
    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "challenge_id"))
    private ChallengeId challengeId;
    
    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "code_version_id"))
    private CodeVersionId codeVersionId;
    
    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "student_id"))
    private StudentId studentId;
    
    @Embedded
    private SolutionDetails details;

    public Solution(CreateSolutionCommand command) {
        this.id = UUID.randomUUID();
        this.challengeId = command.challengeId();
        this.codeVersionId = command.codeVersionId();
        this.studentId = command.studentId();
        this.details = new SolutionDetails(command.code());
    }
}
