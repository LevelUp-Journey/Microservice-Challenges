package com.levelupjourney.microservicechallenges.solutions.domain.model.valueobjects;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Embeddable
public class SolutionDetails {
    
    private Integer attempts;

    @NotNull
    @Setter
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
}
