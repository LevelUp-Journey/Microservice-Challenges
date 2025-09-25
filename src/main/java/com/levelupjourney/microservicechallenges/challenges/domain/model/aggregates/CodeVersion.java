package com.levelupjourney.microservicechallenges.challenges.domain.model.aggregates;

import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.ChallengeId;
import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.CodeLanguage;
import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.CodeVersionId;
import com.levelupjourney.microservicechallenges.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import lombok.Getter;

import java.util.List;

@Getter
@Entity
public class CodeVersion extends AuditableAbstractAggregateRoot<CodeVersion> {
    @Embedded
    private CodeVersionId id;

    @Embedded
    private ChallengeId challengeId;

    private CodeLanguage language;
    private String initialCode;
    private List<CodeVersionTest> tests;
}
