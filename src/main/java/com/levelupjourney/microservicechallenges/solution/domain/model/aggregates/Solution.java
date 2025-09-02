package com.levelupjourney.microservicechallenges.solution.domain.model.aggregates;

import com.levelupjourney.microservicechallenges.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import com.levelupjourney.microservicechallenges.shared.domain.model.valueobjects.Language;
import com.levelupjourney.microservicechallenges.shared.domain.model.valueobjects.ChallengeId;
import com.levelupjourney.microservicechallenges.shared.domain.model.valueobjects.StudentId;
import com.levelupjourney.microservicechallenges.shared.domain.model.valueobjects.TestId;
import com.levelupjourney.microservicechallenges.solution.domain.model.entities.PassedTests;
import com.levelupjourney.microservicechallenges.solution.domain.model.valueobjects.SolutionId;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Entity
@Getter
public class Solution extends AuditableAbstractAggregateRoot<Solution> {

    @EmbeddedId
    private SolutionId id;

    @Embedded
    @AttributeOverride(name = "id", column = @Column(name = "student_id"))
    private StudentId studentId;

    @Embedded
    @AttributeOverride(name = "id", column = @Column(name = "challenge_id"))
    private ChallengeId challengeId;

    @Enumerated(EnumType.STRING)
    private Language language;

    private String code;

    @Embedded
    private PassedTests passedTests;

    // Default constructor for JPA
    protected Solution() {}

    // Constructor for creating new solutions
    public Solution(StudentId studentId, ChallengeId challengeId, Language language, String code) {
        if (studentId == null) throw new IllegalArgumentException("Student ID cannot be null");
        if (challengeId == null) throw new IllegalArgumentException("Challenge ID cannot be null");
        if (language == null) throw new IllegalArgumentException("Language cannot be null");
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("Code cannot be null or empty");
        }
        
        this.id = new SolutionId(UUID.randomUUID());
        this.studentId = studentId;
        this.challengeId = challengeId;
        this.language = language;
        this.code = code.trim();
        this.passedTests = new PassedTests();
    }

    // Business methods
    public void updateCode(String newCode) {
        if (newCode == null || newCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Code cannot be null or empty");
        }
        this.code = newCode.trim();
        // Reset passed tests when code changes
        this.passedTests = new PassedTests();
    }

    public void updateLanguage(Language newLanguage) {
        if (newLanguage == null) {
            throw new IllegalArgumentException("Language cannot be null");
        }
        this.language = newLanguage;
        // Reset passed tests when language changes
        this.passedTests = new PassedTests();
    }

    public void updatePassedTests(List<TestId> passedTestIds) {
        if (passedTestIds == null) {
            throw new IllegalArgumentException("Passed test IDs cannot be null");
        }
        this.passedTests = new PassedTests(passedTestIds);
    }

    public void addPassedTest(TestId testId) {
        if (testId == null) {
            throw new IllegalArgumentException("Test ID cannot be null");
        }
        this.passedTests.addTestId(testId);
    }

    public void removePassedTest(TestId testId) {
        if (testId == null) {
            throw new IllegalArgumentException("Test ID cannot be null");
        }
        this.passedTests.removeTestId(testId);
    }

    public int getPassedTestsCount() {
        return this.passedTests.getTestIdsCount();
    }

    public boolean hasPassedTest(TestId testId) {
        return this.passedTests.hasTestId(testId);
    }
}
