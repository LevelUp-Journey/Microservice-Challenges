package com.levelupjourney.microservicechallenges.solutions.domain.model.valueobjects;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@NoArgsConstructor
@Embeddable
public class SolutionDetails {
    
    private Integer attempts;

    @NotNull
    @Setter
    @Lob
    @Column(columnDefinition = "TEXT")
    private String code;

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastAttemptAt;

    @NotNull
    @Enumerated(EnumType.STRING)
    private SolutionStatus status;

    public SolutionDetails(String code) {
        this.attempts = 0;
        this.status = SolutionStatus.NO_TESTED;
        this.code = code;
    }

    /**
     * Constructor to create SolutionDetails preserving all fields
     * Used when updating only the code
     */
    public SolutionDetails(String code, Integer attempts, Date lastAttemptAt, SolutionStatus status) {
        this.code = code;
        this.attempts = attempts;
        this.lastAttemptAt = lastAttemptAt;
        this.status = status;
    }
}
