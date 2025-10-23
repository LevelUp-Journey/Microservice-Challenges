package com.levelupjourney.microservicechallenges.challenges.domain.services;

import com.levelupjourney.microservicechallenges.challenges.domain.model.aggregates.Challenge;
import com.levelupjourney.microservicechallenges.challenges.domain.model.queries.GetAllChallengeTagsQuery;
import com.levelupjourney.microservicechallenges.challenges.domain.model.queries.GetAllPublishedChallengesQuery;
import com.levelupjourney.microservicechallenges.challenges.domain.model.queries.GetChallengeByIdQuery;
import com.levelupjourney.microservicechallenges.challenges.domain.model.queries.GetChallengesByTeacherIdQuery;
import com.levelupjourney.microservicechallenges.challenges.domain.model.queries.GetPublishedChallengesByTeacherIdQuery;

import java.util.List;
import java.util.Optional;

public interface ChallengeQueryService {
    Optional<Challenge> handle(GetChallengeByIdQuery query);
    List<Challenge> handle(GetAllPublishedChallengesQuery query);
    List<Challenge> handle(GetChallengesByTeacherIdQuery query);
    List<Challenge> handle(GetPublishedChallengesByTeacherIdQuery query);
    List<String> handle(GetAllChallengeTagsQuery query);
}
