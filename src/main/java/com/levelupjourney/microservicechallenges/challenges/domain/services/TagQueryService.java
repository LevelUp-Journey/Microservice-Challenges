package com.levelupjourney.microservicechallenges.challenges.domain.services;

import com.levelupjourney.microservicechallenges.challenges.domain.model.aggregates.Tag;
import com.levelupjourney.microservicechallenges.challenges.domain.model.queries.GetTagByIdQuery;
import com.levelupjourney.microservicechallenges.challenges.domain.model.queries.GetAllTagsQuery;

import java.util.List;
import java.util.Optional;

public interface TagQueryService {
    Optional<Tag> handle(GetTagByIdQuery query);
    List<Tag> handle(GetAllTagsQuery query);
}