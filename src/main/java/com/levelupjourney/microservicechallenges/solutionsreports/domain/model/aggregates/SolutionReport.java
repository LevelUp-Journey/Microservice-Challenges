package com.levelupjourney.microservicechallenges.solutionsreports.domain.model.aggregates;

import com.levelupjourney.microservicechallenges.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import com.levelupjourney.microservicechallenges.solutionsreports.domain.model.commands.CreateSolutionReportCommand;
import com.levelupjourney.microservicechallenges.solutionsreports.domain.model.valueobjects.CodeVersionTestId;
import com.levelupjourney.microservicechallenges.solutionsreports.domain.model.valueobjects.SolutionId;
import com.levelupjourney.microservicechallenges.solutionsreports.domain.model.valueobjects.SolutionReportId;
import com.levelupjourney.microservicechallenges.solutionsreports.domain.model.valueobjects.StudentId;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Getter
@NoArgsConstructor
@Entity
public class SolutionReport extends AuditableAbstractAggregateRoot<SolutionReport> {
    
    @EmbeddedId
    private SolutionReportId id;
    
    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "solution_id"))
    private SolutionId solutionId;
    
    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "student_id"))
    private StudentId studentId;
    
    @ElementCollection
    @CollectionTable(name = "solution_report_successful_tests", joinColumns = @JoinColumn(name = "solution_report_id"))
    @AttributeOverride(name = "value", column = @Column(name = "code_version_test_id"))
    private List<CodeVersionTestId> successfulTests;
    
    private Double timeTaken;
    
    private Double memoryUsed;

    public SolutionReport(CreateSolutionReportCommand command) {
        this.id = new SolutionReportId(UUID.randomUUID());
        this.solutionId = command.solutionId();
        this.studentId = command.studentId();
        this.successfulTests = command.successfulTests();
        this.timeTaken = command.timeTaken();
        this.memoryUsed = command.memoryUsed();
    }
}
