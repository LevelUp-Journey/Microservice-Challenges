package com.levelupjourney.microservicechallenges.challenge.application.internal.commandservices;

import com.levelupjourney.microservicechallenges.challenge.domain.model.aggregates.Challenge;
import com.levelupjourney.microservicechallenges.challenge.domain.model.aggregates.Test;
import com.levelupjourney.microservicechallenges.challenge.domain.model.commands.AddTestToChallengeCommand;
import com.levelupjourney.microservicechallenges.challenge.domain.model.commands.CreateChallengeCommand;
import com.levelupjourney.microservicechallenges.challenge.domain.model.commands.CreateChallengeVersionCommand;
import com.levelupjourney.microservicechallenges.challenge.domain.model.commands.PublishChallengeCommand;
import com.levelupjourney.microservicechallenges.challenge.domain.model.commands.RemoveTestFromChallengeCommand;
import com.levelupjourney.microservicechallenges.challenge.domain.model.commands.StarChallengeByStudentIdCommand;
import com.levelupjourney.microservicechallenges.challenge.domain.model.commands.UnStarChallengeByStudentIdCommand;
import com.levelupjourney.microservicechallenges.challenge.domain.model.commands.UpdateChallengeCommand;
import com.levelupjourney.microservicechallenges.challenge.domain.model.commands.UpdateTestCommand;
import com.levelupjourney.microservicechallenges.challenge.domain.model.entities.ChallengeVersion;
import com.levelupjourney.microservicechallenges.challenge.domain.model.valueobjects.ChallengeState;
import com.levelupjourney.microservicechallenges.challenge.domain.services.ChallengeCommandService;
import com.levelupjourney.microservicechallenges.challenge.infrastructure.persistence.jpa.repository.ChallengeRepository;
import com.levelupjourney.microservicechallenges.challenge.infrastructure.persistence.jpa.repository.ChallengeVersionRepository;
import com.levelupjourney.microservicechallenges.challenge.infrastructure.persistence.jpa.repository.TestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class ChallengeCommandServiceImpl implements ChallengeCommandService {

    private final ChallengeRepository challengeRepository;
    private final ChallengeVersionRepository challengeVersionRepository;
    private final TestRepository testRepository;

    public ChallengeCommandServiceImpl(ChallengeRepository challengeRepository, 
                                     ChallengeVersionRepository challengeVersionRepository,
                                     TestRepository testRepository) {
        this.challengeRepository = challengeRepository;
        this.challengeVersionRepository = challengeVersionRepository;
        this.testRepository = testRepository;
    }

    @Override
    public Optional<Test> handle(AddTestToChallengeCommand command) {
        if (command == null) throw new IllegalArgumentException("Command cannot be null");
        
        // 1. Find the challenge version
        var challengeVersion = challengeVersionRepository.findById(command.challengeVersionId())
                .orElseThrow(() -> new IllegalArgumentException("Challenge version not found"));
        
        // 2. Create new test entity
        var test = new Test(
                command.title(),
                command.hint(),
                command.onErrorHint(),
                command.testCode(),
                command.input(),
                command.expectedOutput()
        );
        
        // 3. Associate test with challenge version
        challengeVersion.addTest(test);
        
        // 4. Save test
        var savedTest = testRepository.save(test);
        
        return Optional.of(savedTest);
    }

    @Override
    public Optional<Challenge> handle(CreateChallengeCommand command) {
        if (command == null) throw new IllegalArgumentException("Command cannot be null");
        
        // 1. Create new Challenge aggregate
        var challenge = new Challenge(
                command.teacherId(),
                command.title(),
                command.description()
        );
        
        // 2. Save challenge (state is already DRAFT from constructor)
        var savedChallenge = challengeRepository.save(challenge);
        
        return Optional.of(savedChallenge);
    }

    @Override
    public Optional<ChallengeVersion> handle(CreateChallengeVersionCommand command) {
        if (command == null) throw new IllegalArgumentException("Command cannot be null");
        
        // 1. Find the parent challenge
        var challenge = challengeRepository.findById(command.challengeId())
                .orElseThrow(() -> new IllegalArgumentException("Challenge not found"));
        
        // 2. Validate challenge is in draft state
        if (challenge.getState() != ChallengeState.DRAFT) {
            throw new IllegalStateException("Cannot add version to non-draft challenge");
        }
        
        // 3. Check if version for this language already exists
        var existingVersion = challengeVersionRepository.findByChallengeAndVersion(challenge, command.language());
        if (existingVersion.isPresent()) {
            throw new IllegalStateException("Version for language " + command.language() + " already exists");
        }
        
        // 4. Create new ChallengeVersion entity
        var challengeVersion = new ChallengeVersion(
                command.language(),
                command.defaultStudentCode()
        );
        
        // 5. Associate with challenge
        challenge.addVersion(challengeVersion);
        
        // 6. Save challenge version
        var savedVersion = challengeVersionRepository.save(challengeVersion);
        
        return Optional.of(savedVersion);
    }

    @Override
    public void handle(PublishChallengeCommand command) {
        if (command == null) throw new IllegalArgumentException("Command cannot be null");
        
        // 1. Find challenge by ID
        var challenge = challengeRepository.findById(command.challengeId())
                .orElseThrow(() -> new IllegalArgumentException("Challenge not found"));
        
        // 2. Validate challenge has at least one version and tests
        if (challenge.getVersions().isEmpty()) {
            throw new IllegalStateException("Challenge must have at least one version to be published");
        }
        
        boolean hasTests = challenge.getVersions().stream()
                .anyMatch(version -> version.getTestsCount() > 0);
        
        if (!hasTests) {
            throw new IllegalStateException("Challenge must have at least one test to be published");
        }
        
        // 3. Change state to PUBLISHED
        challenge.publish();
        
        // 4. Save challenge
        challengeRepository.save(challenge);
    }

    @Override
    public void handle(RemoveTestFromChallengeCommand command) {
        if (command == null) throw new IllegalArgumentException("Command cannot be null");
        
        // 1. Find test by ID
        var test = testRepository.findById(command.testId())
                .orElseThrow(() -> new IllegalArgumentException("Test not found"));
        
        // 2. Get challenge version and validate state
        var challengeVersion = test.getChallengeVersion();
        var challenge = challengeVersion.getChallenge();
        
        if (challenge.getState() != ChallengeState.DRAFT) {
            throw new IllegalStateException("Cannot remove test from non-draft challenge");
        }
        
        // 3. Remove test from challenge version and delete
        challengeVersion.removeTest(test);
        testRepository.delete(test);
    }

    @Override
    public void handle(StarChallengeByStudentIdCommand command) {
        if (command == null) throw new IllegalArgumentException("Command cannot be null");
        
        // 1. Find challenge by ID
        var challenge = challengeRepository.findById(command.challengeId())
                .orElseThrow(() -> new IllegalArgumentException("Challenge not found"));
        
        // 2. Add student to stars list (business logic handles duplicates)
        challenge.starByStudent(command.studentId());
        
        // 3. Save challenge
        challengeRepository.save(challenge);
    }

    @Override
    public void handle(UnStarChallengeByStudentIdCommand command) {
        if (command == null) throw new IllegalArgumentException("Command cannot be null");
        
        // 1. Find challenge by ID
        var challenge = challengeRepository.findById(command.challengeId())
                .orElseThrow(() -> new IllegalArgumentException("Challenge not found"));
        
        // 2. Remove student from stars list
        challenge.unstarByStudent(command.studentId());
        
        // 3. Save challenge
        challengeRepository.save(challenge);
    }

    @Override
    public Optional<Challenge> handle(UpdateChallengeCommand command) {
        if (command == null) throw new IllegalArgumentException("Command cannot be null");
        
        // 1. Find challenge by ID
        var challenge = challengeRepository.findById(command.challengeId())
                .orElseThrow(() -> new IllegalArgumentException("Challenge not found"));
        
        // 2. Validate ownership
        if (!challenge.getTeacherId().equals(command.teacherId())) {
            throw new IllegalStateException("Only the challenge owner can update it");
        }
        
        // 3. Update allowed fields (business logic handles validation)
        if (command.title() != null && !command.title().trim().isEmpty()) {
            challenge.updateTitle(command.title());
        }
        
        if (command.description() != null && !command.description().trim().isEmpty()) {
            challenge.updateDescription(command.description());
        }
        
        // 4. Save challenge
        var savedChallenge = challengeRepository.save(challenge);
        
        return Optional.of(savedChallenge);
    }

    @Override
    public Optional<Test> handle(UpdateTestCommand command) {
        if (command == null) throw new IllegalArgumentException("Command cannot be null");
        
        // 1. Find test by ID
        var test = testRepository.findById(command.testId())
                .orElseThrow(() -> new IllegalArgumentException("Test not found"));
        
        // 2. Validate challenge state
        var challenge = test.getChallengeVersion().getChallenge();
        if (challenge.getState() != ChallengeState.DRAFT) {
            throw new IllegalStateException("Cannot update test in non-draft challenge");
        }
        
        // 3. Update test properties (business logic handles validation)
        if (command.title() != null && !command.title().trim().isEmpty()) {
            test.updateTitle(command.title());
        }
        
        if (command.hint() != null) {
            test.updateHint(command.hint());
        }
        
        if (command.onErrorHint() != null) {
            test.updateOnErrorHint(command.onErrorHint());
        }
        
        if (command.testCode() != null && !command.testCode().trim().isEmpty()) {
            test.updateTestCode(command.testCode());
        }
        
        if (command.input() != null) {
            test.updateInput(command.input());
        }
        
        if (command.expectedOutput() != null && !command.expectedOutput().trim().isEmpty()) {
            test.updateExpectedOutput(command.expectedOutput());
        }
        
        // 4. Save test
        var savedTest = testRepository.save(test);
        
        return Optional.of(savedTest);
    }
}
