package com.levelupjourney.microservicechallenges.challenge.domain.model.queries;

import com.levelupjourney.microservicechallenges.shared.domain.model.valueobjects.StudentId;

public record GetStarredChallengesByStudentIdQuery(StudentId studentId) {
}
