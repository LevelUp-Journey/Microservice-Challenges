package com.levelupjourney.microservicechallenges.challenge.domain.model.aggregates;

import com.levelupjourney.microservicechallenges.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import com.levelupjourney.microservicechallenges.shared.domain.model.valueobjects.TestId;
import com.levelupjourney.microservicechallenges.challenge.domain.model.entities.ChallengeVersion;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
public class Test extends AuditableAbstractAggregateRoot<Test> {

    @EmbeddedId
    private TestId id;

    private String title;
    private String hint;
    private String onErrorHint;

    private String testCode;

    private String input;

    private String expectedOutput;

    @ManyToOne
    @Setter
    @JoinColumn(name = "challenge_version_id", nullable = false)
    private ChallengeVersion challengeVersion;

    // Default constructor for JPA
    protected Test() {}

    // Constructor for creating new tests
    public Test(String title, String hint, String onErrorHint, String testCode, String input, String expectedOutput) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Test title cannot be null or empty");
        }
        if (testCode == null || testCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Test code cannot be null or empty");
        }
        if (expectedOutput == null || expectedOutput.trim().isEmpty()) {
            throw new IllegalArgumentException("Expected output cannot be null or empty");
        }
        
        this.id = new TestId(UUID.randomUUID());
        this.title = title.trim();
        this.hint = hint != null ? hint.trim() : "";
        this.onErrorHint = onErrorHint != null ? onErrorHint.trim() : "";
        this.testCode = testCode.trim();
        this.input = input != null ? input.trim() : "";
        this.expectedOutput = expectedOutput.trim();
    }

    // Business methods
    public void updateTitle(String newTitle) {
        if (newTitle == null || newTitle.trim().isEmpty()) {
            throw new IllegalArgumentException("Test title cannot be null or empty");
        }
        this.title = newTitle.trim();
    }

    public void updateHint(String newHint) {
        this.hint = newHint != null ? newHint.trim() : "";
    }

    public void updateOnErrorHint(String newOnErrorHint) {
        this.onErrorHint = newOnErrorHint != null ? newOnErrorHint.trim() : "";
    }

    public void updateTestCode(String newTestCode) {
        if (newTestCode == null || newTestCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Test code cannot be null or empty");
        }
        this.testCode = newTestCode.trim();
    }

    public void updateInput(String newInput) {
        this.input = newInput != null ? newInput.trim() : "";
    }

    public void updateExpectedOutput(String newExpectedOutput) {
        if (newExpectedOutput == null || newExpectedOutput.trim().isEmpty()) {
            throw new IllegalArgumentException("Expected output cannot be null or empty");
        }
        this.expectedOutput = newExpectedOutput.trim();
    }
}
