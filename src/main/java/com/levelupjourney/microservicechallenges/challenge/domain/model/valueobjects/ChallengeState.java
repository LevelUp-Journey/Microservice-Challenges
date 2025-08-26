package com.levelupjourney.microservicechallenges.challenge.domain.model.valueobjects;

public enum ChallengeState {
    DRAFT, // State when a challenge is being created
    PUBLISHED, // State when a challenge is live and available to users
    ARCHIVED, // State when a challenge is no longer active but kept for record
    DELETED // State when a challenge is removed from the system
}
