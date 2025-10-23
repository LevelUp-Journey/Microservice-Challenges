package com.levelupjourney.microservicechallenges.challenges.domain.model.aggregates;

import com.levelupjourney.microservicechallenges.challenges.domain.model.commands.AddCodeVersionCommand;
import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.ChallengeId;
import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.CodeLanguage;
import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.CodeVersionId;
import com.levelupjourney.microservicechallenges.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@NoArgsConstructor
@Entity
public class CodeVersion extends AuditableAbstractAggregateRoot<CodeVersion> {
    
    @EmbeddedId
    private CodeVersionId id;

    @Embedded
    @AttributeOverride(name = "id", column = @Column(name = "challenge_id"))
    private ChallengeId challengeId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CodeLanguage language;
    
    @Column(name = "initial_code", columnDefinition = "TEXT")
    private String initialCode;

    @Column(name = "function_name")
    private String functionName;

    @OneToMany(mappedBy = "codeVersionId", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<CodeVersionTest> tests = new ArrayList<>();

    public CodeVersion(AddCodeVersionCommand command) {
        this.id = new CodeVersionId(UUID.randomUUID());
        this.challengeId = command.challengeId();
        this.language = command.language();
        this.initialCode = command.defaultCode() != null ? command.defaultCode() : ""; // Use default code from command
        this.functionName = command.functionName() != null ? command.functionName() : "main";
    }
    
    // Business methods
    public void updateInitialCode(String newInitialCode) {
        this.initialCode = newInitialCode != null ? newInitialCode : "";
    }

    public void updateFunctionName(String newFunctionName) {
        this.functionName = newFunctionName != null ? newFunctionName : "main";
    }
}
