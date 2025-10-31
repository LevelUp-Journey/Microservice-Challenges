package com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects;

public enum Difficulty {
    EASY(20),
    MEDIUM(40),
    HARD(50),
    EXPERT(60);

    private final int maxScore;

    Difficulty(int maxScore) {
        this.maxScore = maxScore;
    }

    public int getMaxScore() {
        return maxScore;
    }
}