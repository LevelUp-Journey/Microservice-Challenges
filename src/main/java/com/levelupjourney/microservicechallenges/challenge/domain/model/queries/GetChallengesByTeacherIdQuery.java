package com.levelupjourney.microservicechallenges.challenge.domain.model.queries;

import com.levelupjourney.microservicechallenges.challenge.domain.model.valueobjects.TeacherId;

public record GetChallengesByTeacherIdQuery(TeacherId teacherId) {
}
