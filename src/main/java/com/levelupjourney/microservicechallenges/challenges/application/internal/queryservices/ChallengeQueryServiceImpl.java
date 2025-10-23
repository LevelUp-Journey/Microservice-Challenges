package com.levelupjourney.microservicechallenges.challenges.application.internal.queryservices;

import com.levelupjourney.microservicechallenges.challenges.domain.model.aggregates.Challenge;
import com.levelupjourney.microservicechallenges.challenges.domain.model.queries.GetAllChallengeTagsQuery;
import com.levelupjourney.microservicechallenges.challenges.domain.model.queries.GetAllPublishedChallengesQuery;
import com.levelupjourney.microservicechallenges.challenges.domain.model.queries.GetChallengeByIdQuery;
import com.levelupjourney.microservicechallenges.challenges.domain.model.queries.GetChallengesByTeacherIdQuery;
import com.levelupjourney.microservicechallenges.challenges.domain.model.queries.GetPublishedChallengesByTeacherIdQuery;
import com.levelupjourney.microservicechallenges.challenges.domain.services.ChallengeQueryService;
import com.levelupjourney.microservicechallenges.challenges.infrastructure.persistence.jpa.repositories.ChallengeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ChallengeQueryServiceImpl implements ChallengeQueryService {

    private final ChallengeRepository challengeRepository;

    public ChallengeQueryServiceImpl(ChallengeRepository challengeRepository) {
        this.challengeRepository = challengeRepository;
    }

    @Override
    public Optional<Challenge> handle(GetChallengeByIdQuery query) {
        return challengeRepository.findById(query.challengeId());
    }

    @Override
    public List<Challenge> handle(GetAllPublishedChallengesQuery query) {
        return challengeRepository.findPublishedChallenges();
    }

    @Override
    public List<Challenge> handle(GetChallengesByTeacherIdQuery query) {
        return challengeRepository.findByTeacherId(query.teacherId().id());
    }

    @Override
    public List<Challenge> handle(GetPublishedChallengesByTeacherIdQuery query) {
        return challengeRepository.findPublishedChallengesByTeacherId(query.teacherId().id());
    }

    @Override
    public List<String> handle(GetAllChallengeTagsQuery query) {
        // Get all challenges and extract their tags as strings
        return challengeRepository.findAll()
                .stream()
                .flatMap(challenge -> challenge.getTags().stream())
                .distinct()
                .collect(Collectors.toList());
    }
}
