package com.levelupjourney.microservicechallenges.challenge.domain.model.entities;

import com.levelupjourney.microservicechallenges.challenge.domain.model.aggregates.Test;
import com.levelupjourney.microservicechallenges.shared.domain.model.valueobjects.Language;
import jakarta.persistence.*;
import java.util.List;

@Embeddable
public class ChallengeVersion {

    @Enumerated(EnumType.STRING)
    private Language version;

    private String defaultStudentCode;

    @ElementCollection
    @CollectionTable(name = "challenge_version_tests")
    private List<Test> tests;
}
