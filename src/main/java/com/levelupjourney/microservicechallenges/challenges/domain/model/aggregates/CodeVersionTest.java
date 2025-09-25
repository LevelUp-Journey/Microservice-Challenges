package com.levelupjourney.microservicechallenges.challenges.domain.model.aggregates;

import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.CodeVersionId;
import com.levelupjourney.microservicechallenges.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Entity
public class CodeVersionTest extends AuditableAbstractAggregateRoot<CodeVersionTest> {
    
    @Id
    private UUID id;
    
    @Embedded
    private CodeVersionId codeVersionId;
    
    private String input;
    
    private String expectedOutput;
    
    private String customValidationCode;
    
    private String failureMessage;
}
