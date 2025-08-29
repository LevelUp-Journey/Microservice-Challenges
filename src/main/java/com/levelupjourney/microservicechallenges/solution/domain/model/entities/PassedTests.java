package com.levelupjourney.microservicechallenges.solution.domain.model.entities;

import com.levelupjourney.microservicechallenges.shared.domain.model.valueobjects.TestId;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Embeddable
public class PassedTests {

    @ElementCollection
    @CollectionTable(
        name = "solution_passed_tests",
        joinColumns = @JoinColumn(name = "solution_id")
    )
    private List<TestId> testIds;

    // Default constructor
    public PassedTests() {
        this.testIds = new ArrayList<>();
    }

    // Constructor with test IDs
    public PassedTests(List<TestId> testIds) {
        this.testIds = testIds != null ? new ArrayList<>(testIds) : new ArrayList<>();
    }

    // Business methods
    public void addTestId(TestId testId) {
        if (testId != null && !this.testIds.contains(testId)) {
            this.testIds.add(testId);
        }
    }

    public void removeTestId(TestId testId) {
        if (testId != null) {
            this.testIds.remove(testId);
        }
    }

    public boolean hasTestId(TestId testId) {
        return testId != null && this.testIds.contains(testId);
    }

    public int getTestIdsCount() {
        return this.testIds.size();
    }

    public void clear() {
        this.testIds.clear();
    }

    // Getters
    public List<TestId> getTestIds() {
        return new ArrayList<>(this.testIds);
    }
}
