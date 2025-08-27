package com.levelupjourney.microservicechallenges.challenge.domain.model.entities;

import com.levelupjourney.microservicechallenges.challenge.domain.model.aggregates.Test;
import com.levelupjourney.microservicechallenges.shared.domain.model.valueobjects.Language;

public class ChallengeVersion {

    private Language version;
    private String defaultStudentCode;

    private Test[] tests;
}
