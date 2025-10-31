package com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects;

public enum Difficulty {
    EASY(5),
    MEDIUM(10),
    HARD(20),
    EXPERT(40);

    private final int maxScore;

    Difficulty(int maxScore) {
        this.maxScore = maxScore;
    }

    public int getMaxScore() {
        return maxScore;
    }
}