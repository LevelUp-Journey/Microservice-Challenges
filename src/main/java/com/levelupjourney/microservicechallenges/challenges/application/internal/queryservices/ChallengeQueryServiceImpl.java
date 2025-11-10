package com.levelupjourney.microservicechallenges.challenges.application.internal.queryservices;

import com.levelupjourney.microservicechallenges.challenges.domain.model.aggregates.Challenge;
import com.levelupjourney.microservicechallenges.challenges.domain.model.queries.GetAllChallengeTagsQuery;
import com.levelupjourney.microservicechallenges.challenges.domain.model.queries.GetAllPublishedChallengesQuery;
import com.levelupjourney.microservicechallenges.challenges.domain.model.queries.GetChallengeByIdQuery;
import com.levelupjourney.microservicechallenges.challenges.domain.model.queries.GetChallengesByTeacherIdQuery;
import com.levelupjourney.microservicechallenges.challenges.domain.model.queries.GetPublishedChallengesByTeacherIdQuery;
import com.levelupjourney.microservicechallenges.challenges.domain.model.queries.SearchPublishedChallengesQuery;
import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.Difficulty;
import com.levelupjourney.microservicechallenges.challenges.domain.services.ChallengeQueryService;
import com.levelupjourney.microservicechallenges.challenges.infrastructure.persistence.jpa.repositories.ChallengeRepository;
import org.springframework.stereotype.Service;

import java.util.Arrays;
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

    @Override
    public List<Challenge> handle(SearchPublishedChallengesQuery query) {
        // If no filters provided, return all published challenges
        if (!query.hasNameFilter() && !query.hasDifficultyFilter()) {
            return challengeRepository.findPublishedChallenges();
        }

        // Parse difficulty if provided
        Difficulty difficulty = null;
        if (query.hasDifficultyFilter()) {
            try {
                difficulty = Difficulty.valueOf(query.difficulty().toUpperCase());
            } catch (IllegalArgumentException e) {
                // Invalid difficulty value, ignore filter
                difficulty = null;
            }
        }

        // Apply filters based on what's provided
        List<Challenge> results;
        if (query.hasNameFilter() && difficulty != null) {
            // Both name and difficulty filters
            results = challengeRepository.searchPublishedChallengesByNameAndDifficulty(query.name(), difficulty);
        } else if (query.hasNameFilter()) {
            // Only name filter
            results = challengeRepository.searchPublishedChallengesByName(query.name());
        } else if (difficulty != null) {
            // Only difficulty filter
            results = challengeRepository.searchPublishedChallengesByDifficulty(difficulty);
        } else {
            // No valid filters, return all published
            results = challengeRepository.findPublishedChallenges();
        }

        // Apply tags filter if provided (in-memory filtering)
        if (query.hasTagsFilter()) {
            List<String> searchTags = Arrays.stream(query.tags().split(","))
                .map(String::trim)
                .map(String::toLowerCase)
                .collect(Collectors.toList());

            results = results.stream()
                .filter(challenge -> {
                    List<String> challengeTags = challenge.getTags().stream()
                        .map(String::toLowerCase)
                        .collect(Collectors.toList());
                    // Check if challenge has at least one of the search tags
                    return searchTags.stream().anyMatch(challengeTags::contains);
                })
                .collect(Collectors.toList());
        }

        return results;
    }
}
