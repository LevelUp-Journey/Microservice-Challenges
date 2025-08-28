package com.levelupjourney.microservicechallenges.challenge.domain.model.aggregates;

import com.levelupjourney.microservicechallenges.shared.domain.model.valueobjects.TestId;
import jakarta.persistence.*;

@Embeddable
public class Test {

    @Embedded
    private TestId id;

    private String title;
    private String hint;
    private String onErrorHint;

    private String testCode;

    private String input;

    private String expectedOutput;
}
