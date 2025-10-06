package com.levelupjourney.microservicechallenges.challenges.domain.model.queries;

import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.TagId;

public record GetTagByIdQuery(
        TagId tagId
) {
}