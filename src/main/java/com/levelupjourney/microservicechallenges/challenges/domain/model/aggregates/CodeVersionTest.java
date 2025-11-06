package com.levelupjourney.microservicechallenges.challenges.domain.model.aggregates;

import com.levelupjourney.microservicechallenges.challenges.domain.model.commands.AddCodeVersionTestCommand;
import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.CodeVersionId;
import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.CodeVersionTestId;
import com.levelupjourney.microservicechallenges.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
@Entity
public class CodeVersionTest extends AuditableAbstractAggregateRoot<CodeVersionTest> {
    
    @EmbeddedId
    private CodeVersionTestId id;
    
    @Embedded
    @AttributeOverride(name = "id", column = @Column(name = "code_version_id"))
    private CodeVersionId codeVersionId;
    
    @Lob
    @Column(columnDefinition = "TEXT")
    private String input;
    
    @Lob
    @Column(columnDefinition = "TEXT")
    private String expectedOutput;
    
    @Lob
    @Column(columnDefinition = "TEXT")
    private String customValidationCode;
    
    @Lob
    @Column(columnDefinition = "TEXT")
    private String failureMessage;

    @Column(name = "is_secret", nullable = false)
    private Boolean isSecret;

    public CodeVersionTest(AddCodeVersionTestCommand command) {
        this.id = new CodeVersionTestId(UUID.randomUUID());
        this.codeVersionId = command.codeVersionId();
        this.input = command.input();
        this.expectedOutput = command.expectedOutput();
        this.customValidationCode = command.customValidationCode();
        this.failureMessage = command.failureMessage();
        this.isSecret = command.isSecret() != null ? command.isSecret() : false;
    }
    
    // Business methods
    public void updateTestDetails(String input, String expectedOutput, String customValidationCode, String failureMessage, Boolean isSecret) {
        if (input != null) {
            this.input = input;
        }
        if (expectedOutput != null) {
            this.expectedOutput = expectedOutput;
        }
        if (customValidationCode != null) {
            this.customValidationCode = customValidationCode;
        }
        if (failureMessage != null) {
            this.failureMessage = failureMessage;
        }
        if (isSecret != null) {
            this.isSecret = isSecret;
        }
    }
}
