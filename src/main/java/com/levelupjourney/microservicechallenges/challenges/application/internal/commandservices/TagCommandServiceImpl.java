package com.levelupjourney.microservicechallenges.challenges.application.internal.commandservices;

import com.levelupjourney.microservicechallenges.challenges.domain.model.aggregates.Tag;
import com.levelupjourney.microservicechallenges.challenges.domain.model.commands.CreateTagCommand;
import com.levelupjourney.microservicechallenges.challenges.domain.model.commands.UpdateTagCommand;
import com.levelupjourney.microservicechallenges.challenges.domain.model.commands.DeleteTagCommand;
import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.TagId;
import com.levelupjourney.microservicechallenges.challenges.domain.services.TagCommandService;
import com.levelupjourney.microservicechallenges.challenges.infrastructure.persistence.jpa.repositories.TagRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class TagCommandServiceImpl implements TagCommandService {

    private final TagRepository tagRepository;

    public TagCommandServiceImpl(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Override
    @Transactional
    public TagId handle(CreateTagCommand command) {
        // Check if tag with same name already exists
        if (tagRepository.existsByName(command.name())) {
            throw new IllegalArgumentException("Tag with name '" + command.name() + "' already exists");
        }

        // Create new tag
        Tag tag = new Tag(command.name(), command.color(), command.iconUrl());

        // Save tag
        Tag savedTag = tagRepository.save(tag);
        return savedTag.getId();
    }

    @Override
    @Transactional
    public void handle(UpdateTagCommand command) {
        // Find tag by ID
        Tag tag = tagRepository.findById(command.tagId())
                .orElseThrow(() -> new IllegalArgumentException("Tag not found with ID: " + command.tagId().id()));

        // Check if new name conflicts with existing tag (if name is being changed)
        if (command.name() != null && !command.name().equals(tag.getName())) {
            if (tagRepository.existsByName(command.name())) {
                throw new IllegalArgumentException("Tag with name '" + command.name() + "' already exists");
            }
        }

        // Update tag details
        tag.updateDetails(command.name(), command.color(), command.iconUrl());

        // Save updated tag
        tagRepository.save(tag);
    }

    @Override
    @Transactional
    public void handle(DeleteTagCommand command) {
        // Find tag by ID
        Tag tag = tagRepository.findById(command.tagId())
                .orElseThrow(() -> new IllegalArgumentException("Tag not found with ID: " + command.tagId().id()));

        // Delete tag (will be automatically removed from challenges due to many-to-many relationship)
        tagRepository.delete(tag);
    }
}