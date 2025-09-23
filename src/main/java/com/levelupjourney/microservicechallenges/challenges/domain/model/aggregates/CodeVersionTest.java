package com.levelupjourney.microservicechallenges.challenges.domain.model.aggregates;

import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.CodeVersionId;
import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.CodeVersionTestId;
import com.levelupjourney.microservicechallenges.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import lombok.Getter;

@Getter
public class CodeVersionTest extends AuditableAbstractAggregateRoot<CodeVersionTest> {
    private CodeVersionTestId id;
    private CodeVersionId codeVersionId;
    private String input;
    private String expectedOutput;
    private String customValidationCode;
    private String failureMessage;
}
