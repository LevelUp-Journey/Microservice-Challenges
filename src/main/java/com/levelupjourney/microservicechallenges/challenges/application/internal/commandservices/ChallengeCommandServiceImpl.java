package com.levelupjourney.microservicechallenges.challenges.application.internal.commandservices;

import com.levelupjourney.microservicechallenges.challenges.domain.model.aggregates.Challenge;
import com.levelupjourney.microservicechallenges.challenges.domain.model.commands.CreateChallengeCommand;
import com.levelupjourney.microservicechallenges.challenges.domain.model.commands.PublishChallengeCommand;
import com.levelupjourney.microservicechallenges.challenges.domain.model.commands.StartChallengeCommand;
import com.levelupjourney.microservicechallenges.challenges.domain.model.commands.UpdateChallengeCommand;
import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.ChallengeId;
import com.levelupjourney.microservicechallenges.challenges.domain.services.ChallengeCommandService;
import com.levelupjourney.microservicechallenges.challenges.infrastructure.persistence.jpa.repositories.ChallengeRepository;
import org.springframework.stereotype.Service;

@Service
public class ChallengeCommandServiceImpl implements ChallengeCommandService {

    private final ChallengeRepository challengeRepository;

    public ChallengeCommandServiceImpl(ChallengeRepository challengeRepository) {
        this.challengeRepository = challengeRepository;
    }

    @Override
    public ChallengeId handle(CreateChallengeCommand command) {
        // Create new challenge with DRAFT status using constructor
        Challenge challenge = new Challenge(command);
        
        // Save to database
        Challenge savedChallenge = challengeRepository.save(challenge);
        return new ChallengeId(savedChallenge.getId());
    }

    @Override
    public ChallengeId handle(PublishChallengeCommand command) {
        // Find the challenge by ID
        Challenge challenge = challengeRepository.findById(command.challengeId().value())
                .orElseThrow(() -> new IllegalArgumentException("Challenge not found"));

        // Use business method to publish the challenge
        challenge.publish();

        // Save the updated challenge
        challengeRepository.save(challenge);
        
        return new ChallengeId(challenge.getId());
    }

    @Override
    public void handle(StartChallengeCommand command) {
        // Find the challenge by ID
        Challenge challenge = challengeRepository.findById(command.challengeId().value())
                .orElseThrow(() -> new IllegalArgumentException("Challenge not found"));

        // Validate that the challenge can be started
        challenge.validateCanStart();

        // Note: StartChallengeCommand might be used for other purposes
        // like initializing something for the specific user
    }

    @Override
    public void handle(UpdateChallengeCommand command) {
        // Find the challenge by ID
        Challenge challenge = challengeRepository.findById(command.challengeId().value())
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
