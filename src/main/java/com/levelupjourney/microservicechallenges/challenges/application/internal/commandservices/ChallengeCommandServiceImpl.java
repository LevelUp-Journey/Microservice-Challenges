package com.levelupjourney.microservicechallenges.challenges.application.internal.commandservices;

import com.levelupjourney.microservicechallenges.challenges.domain.model.aggregates.Challenge;
import com.levelupjourney.microservicechallenges.challenges.domain.model.aggregates.CodeVersion;
import com.levelupjourney.microservicechallenges.challenges.domain.model.commands.CreateChallengeCommand;
import com.levelupjourney.microservicechallenges.challenges.domain.model.commands.PublishChallengeCommand;
import com.levelupjourney.microservicechallenges.challenges.domain.model.commands.StartChallengeCommand;
import com.levelupjourney.microservicechallenges.challenges.domain.model.commands.UpdateChallengeCommand;
import com.levelupjourney.microservicechallenges.challenges.domain.model.events.ChallengeStartedEvent;
import com.levelupjourney.microservicechallenges.challenges.domain.model.queries.GetCodeVersionByIdQuery;
import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.ChallengeId;
import com.levelupjourney.microservicechallenges.challenges.domain.services.ChallengeCommandService;
import com.levelupjourney.microservicechallenges.challenges.domain.services.CodeVersionQueryService;
import com.levelupjourney.microservicechallenges.challenges.infrastructure.persistence.jpa.repositories.ChallengeRepository;
import jakarta.transaction.Transactional;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class ChallengeCommandServiceImpl implements ChallengeCommandService {

    private final ChallengeRepository challengeRepository;
    private final CodeVersionQueryService codeVersionQueryService;
    private final ApplicationEventPublisher eventPublisher;

    public ChallengeCommandServiceImpl(ChallengeRepository challengeRepository,
                                     CodeVersionQueryService codeVersionQueryService,
                                     ApplicationEventPublisher eventPublisher) {
        this.challengeRepository = challengeRepository;
        this.codeVersionQueryService = codeVersionQueryService;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public ChallengeId handle(CreateChallengeCommand command) {
        // Create new challenge with DRAFT status using constructor
        Challenge challenge = new Challenge(command);
        
        // Save to database
        Challenge savedChallenge = challengeRepository.save(challenge);
        return savedChallenge.getId();
    }

    @Override
    @Transactional
    public ChallengeId handle(PublishChallengeCommand command) {
        // Find the challenge by ID
        Challenge challenge = challengeRepository.findById(command.challengeId())
                .orElseThrow(() -> new IllegalArgumentException("Challenge not found"));

        // Use business method to publish the challenge
        challenge.publish();

        // Save the updated challenge
        challengeRepository.save(challenge);
        
        return challenge.getId();
    }

    @Override
    @Transactional
    public void handle(StartChallengeCommand command) {
        // Find the challenge by ID
        Challenge challenge = challengeRepository.findById(command.challengeId())
                .orElseThrow(() -> new IllegalArgumentException("Challenge not found"));

        // Validate that the challenge can be started
        challenge.validateCanStart();

        // Get the code version to obtain default code
        CodeVersion codeVersion = codeVersionQueryService.handle(new GetCodeVersionByIdQuery(command.codeVersionId()))
                .orElseThrow(() -> new IllegalArgumentException("Code version not found"));

        // Publish domain event to trigger business policies
        ChallengeStartedEvent event = new ChallengeStartedEvent(
            command.studentId(),
            command.challengeId(),
            command.codeVersionId(),
            codeVersion.getInitialCode()
        );
        eventPublisher.publishEvent(event);
    }

    @Override
    @Transactional
    public void handle(UpdateChallengeCommand command) {
        // Find the challenge by ID
        Challenge challenge = challengeRepository.findById(command.challengeId())
                .orElseThrow(() -> new IllegalArgumentException("Challenge not found"));

        // Use business method to update challenge details with Optional handling
        challenge.updateDetails(
            command.name().orElse(null),
            command.description().orElse(null),
            command.experiencePoints().orElse(null),
            command.tags().orElse(null)
        );

        // Save the updated challenge
        challengeRepository.save(challenge);
    }
}
