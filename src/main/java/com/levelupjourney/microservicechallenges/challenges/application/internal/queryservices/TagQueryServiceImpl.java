package com.levelupjourney.microservicechallenges.challenges.application.internal.queryservices;

import com.levelupjourney.microservicechallenges.challenges.domain.model.aggregates.Tag;
import com.levelupjourney.microservicechallenges.challenges.domain.model.queries.GetTagByIdQuery;
import com.levelupjourney.microservicechallenges.challenges.domain.model.queries.GetAllTagsQuery;
import com.levelupjourney.microservicechallenges.challenges.domain.services.TagQueryService;
import com.levelupjourney.microservicechallenges.challenges.infrastructure.persistence.jpa.repositories.TagRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TagQueryServiceImpl implements TagQueryService {

    private final TagRepository tagRepository;

    public TagQueryServiceImpl(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Override
    public Optional<Tag> handle(GetTagByIdQuery query) {
        return tagRepository.findById(query.tagId());
    }

    @Override
    public List<Tag> handle(GetAllTagsQuery query) {
        return tagRepository.findAllOrderByName();
    }
}