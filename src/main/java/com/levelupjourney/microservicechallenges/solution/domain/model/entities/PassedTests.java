package com.levelupjourney.microservicechallenges.solution.domain.model.entities;

import com.levelupjourney.microservicechallenges.shared.domain.model.valueobjects.TestId;
import jakarta.persistence.*;
import java.util.List;

@Embeddable
public class PassedTests {

    @ElementCollection
    @CollectionTable(
        name = "solution_passed_tests",
        joinColumns = @JoinColumn(name = "solution_id")
    )
    private List<TestId> testIds;
}
