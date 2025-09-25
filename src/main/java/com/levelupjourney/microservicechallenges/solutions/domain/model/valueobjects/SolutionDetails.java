package com.levelupjourney.microservicechallenges.solutions.domain.model.valueobjects;

import java.util.Date;

public class SolutionDetails {
    private Integer attempts;
    private String code;
    private Date lastAttemptAt;
    private SolutionStatus status;
}
