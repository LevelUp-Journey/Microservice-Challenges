package com.levelupjourney.microservicechallenges.challenges.domain.model.queries;

import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.TeacherId;

/**
 * Query to get only PUBLISHED challenges by teacher ID.
 * Used when students want to see a teacher's challenges.
 */
public record GetPublishedChallengesByTeacherIdQuery(
        TeacherId teacherId
) {
}
