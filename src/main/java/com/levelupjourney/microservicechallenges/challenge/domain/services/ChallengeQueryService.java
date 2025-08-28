package com.levelupjourney.microservicechallenges.challenge.domain.services;

import com.levelupjourney.microservicechallenges.challenge.domain.model.aggregates.Challenge;
import com.levelupjourney.microservicechallenges.challenge.domain.model.aggregates.Test;
import com.levelupjourney.microservicechallenges.challenge.domain.model.queries.GetAllChallengesQuery;
import com.levelupjourney.microservicechallenges.challenge.domain.model.queries.GetChallengeByIdQuery;
import com.levelupjourney.microservicechallenges.challenge.domain.model.queries.GetChallengesByStateQuery;
import com.levelupjourney.microservicechallenges.challenge.domain.model.queries.GetChallengesByTeacherIdQuery;
import com.levelupjourney.microservicechallenges.challenge.domain.model.queries.GetChallengeStarsAmountQuery;
import com.levelupjourney.microservicechallenges.challenge.domain.model.queries.GetChallengeTestsByChallengeIdQuery;
import com.levelupjourney.microservicechallenges.challenge.domain.model.queries.GetStarredChallengesByStudentIdQuery;
import com.levelupjourney.microservicechallenges.challenge.domain.model.queries.GetTestByIdQuery;

import java.util.List;
import java.util.Optional;

public interface ChallengeQueryService {
    public List<Challenge> handle(GetAllChallengesQuery query);
    public Optional<Challenge> handle(GetChallengeByIdQuery query);
    public List<Challenge> handle(GetChallengesByStateQuery query);
    public List<Challenge> handle(GetChallengesByTeacherIdQuery query);
    public int handle(GetChallengeStarsAmountQuery query);
    public List<Test> handle(GetChallengeTestsByChallengeIdQuery query);
    public List<Challenge> handle(GetStarredChallengesByStudentIdQuery query);
    public Optional<Test> handle(GetTestByIdQuery query);
}
