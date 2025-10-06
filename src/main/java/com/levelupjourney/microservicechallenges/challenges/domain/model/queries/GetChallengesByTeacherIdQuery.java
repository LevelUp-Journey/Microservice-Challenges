package com.levelupjourney.microservicechallenges.challenges.domain.model.queries;

import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.TeacherId;

public record GetChallengesByTeacherIdQuery(
        TeacherId teacherId
) {
}
