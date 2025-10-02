package com.levelupjourney.microservicechallenges.challenges.application.internal.commandservices;

import com.levelupjourney.microservicechallenges.challenges.domain.model.aggregates.Challenge;
import com.levelupjourney.microservicechallenges.challenges.domain.model.aggregates.CodeVersion;
import com.levelupjourney.microservicechallenges.challenges.domain.model.aggregates.Tag;
import com.levelupjourney.microservicechallenges.challenges.domain.model.commands.CreateChallengeCommand;
import com.levelupjourney.microservicechallenges.challenges.domain.model.commands.StartChallengeCommand;
import com.levelupjourney.microservicechallenges.challenges.domain.model.commands.StartChallengeResult;
import com.levelupjourney.microservicechallenges.challenges.domain.model.commands.UpdateChallengeCommand;
import com.levelupjourney.microservicechallenges.challenges.domain.model.commands.AssignTagToChallengeCommand;
import com.levelupjourney.microservicechallenges.challenges.domain.model.commands.UnassignTagFromChallengeCommand;
import com.levelupjourney.microservicechallenges.challenges.domain.model.events.ChallengeStartedEvent;
import com.levelupjourney.microservicechallenges.challenges.domain.model.queries.GetCodeVersionByIdQuery;
import com.levelupjourney.microservicechallenges.challenges.domain.model.queries.GetTagByIdQuery;
import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.ChallengeId;
import com.levelupjourney.microservicechallenges.challenges.domain.services.ChallengeCommandService;
import com.levelupjourney.microservicechallenges.challenges.domain.services.CodeVersionQueryService;
import com.levelupjourney.microservicechallenges.challenges.domain.services.TagQueryService;
import com.levelupjourney.microservicechallenges.challenges.infrastructure.persistence.jpa.repositories.ChallengeRepository;
import com.levelupjourney.microservicechallenges.solutions.interfaces.acl.SolutionsAcl;
import jakarta.transaction.Transactional;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class ChallengeCommandServiceImpl implements ChallengeCommandService {

    private final ChallengeRepository challengeRepository;
    private final CodeVersionQueryService codeVersionQueryService;
    private final TagQueryService tagQueryService;
    private final ApplicationEventPublisher eventPublisher;
    private final SolutionsAcl solutionsAcl;

    public ChallengeCommandServiceImpl(ChallengeRepository challengeRepository,
                                     CodeVersionQueryService codeVersionQueryService,
                                     TagQueryService tagQueryService,
                                     ApplicationEventPublisher eventPublisher,
                                     SolutionsAcl solutionsAcl) {
        this.challengeRepository = challengeRepository;
        this.codeVersionQueryService = codeVersionQueryService;
        this.tagQueryService = tagQueryService;
        this.eventPublisher = eventPublisher;
        this.solutionsAcl = solutionsAcl;
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
    public StartChallengeResult handle(StartChallengeCommand command) {
        // Check if solution already exists for this student, challenge, and code version
        // Pass primitive types (String) to ACL to maintain decoupling
        String existingSolutionId = solutionsAcl.getExistingSolutionId(
            command.studentId().id().toString(),
            command.challengeId().id().toString(),
            command.codeVersionId().id().toString()
        );
        
        // If solution already exists, return it without creating a new one
        if (existingSolutionId != null) {
            return new StartChallengeResult(existingSolutionId, false);
        }
        
        // Find the challenge by ID
        Challenge challenge = challengeRepository.findById(command.challengeId())
                .orElseThrow(() -> new IllegalArgumentException("Challenge not found"));

        // Validate that the challenge can be started
        challenge.validateCanStart();

        // Get the code version to obtain default code
        CodeVersion codeVersion = codeVersionQueryService.handle(new GetCodeVersionByIdQuery(command.codeVersionId()))
                .orElseThrow(() -> new IllegalArgumentException("Code version not found"));

        // Publish domain event to trigger business policies (this will create the solution)
        ChallengeStartedEvent event = new ChallengeStartedEvent(
            command.studentId(),
            command.challengeId(),
            command.codeVersionId(),
            codeVersion.getInitialCode()
        );
        eventPublisher.publishEvent(event);
        
        // After event is processed, get the newly created solution ID
        // Pass primitive types to ACL
        String newSolutionId = solutionsAcl.getExistingSolutionId(
            command.studentId().id().toString(),
            command.challengeId().id().toString(),
            command.codeVersionId().id().toString()
        );
        
        return new StartChallengeResult(newSolutionId, true);
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
            command.status().orElse(null),
            command.tags().orElse(null)
        );

        // Save the updated challenge
        challengeRepository.save(challenge);
    }

    @Override
    @Transactional
    public void handle(AssignTagToChallengeCommand command) {
        // Find the challenge by ID
        Challenge challenge = challengeRepository.findById(command.challengeId())
                .orElseThrow(() -> new IllegalArgumentException("Challenge not found with ID: " + command.challengeId().id()));

        // Find the tag by ID
        Tag tag = tagQueryService.handle(new GetTagByIdQuery(command.tagId()))
                .orElseThrow(() -> new IllegalArgumentException("Tag not found with ID: " + command.tagId().id()));

        // Add tag to challenge using business method
        challenge.addTag(tag);

        // Save the updated challenge
        challengeRepository.save(challenge);
    }

    @Override
    @Transactional
    public void handle(UnassignTagFromChallengeCommand command) {
        // Find the challenge by ID
        Challenge challenge = challengeRepository.findById(command.challengeId())
                .orElseThrow(() -> new IllegalArgumentException("Challenge not found with ID: " + command.challengeId().id()));

        // Find the tag by ID
        Tag tag = tagQueryService.handle(new GetTagByIdQuery(command.tagId()))
                .orElseThrow(() -> new IllegalArgumentException("Tag not found with ID: " + command.tagId().id()));

        // Remove tag from challenge using business method
        challenge.removeTag(tag);

        // Save the updated challenge
        challengeRepository.save(challenge);
    }
}
