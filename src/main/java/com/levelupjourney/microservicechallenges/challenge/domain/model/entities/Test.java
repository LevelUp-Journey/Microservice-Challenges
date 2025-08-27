package com.levelupjourney.microservicechallenges.challenge.domain.model.entities;

import com.levelupjourney.microservicechallenges.shared.domain.model.valueobjects.TestId;

public class Test {
    private TestId id;
    private String title;
    private String hint;
    private String onErrorHint;
    private String testCode;

    private String input;
    private String expectedOutput;
}
