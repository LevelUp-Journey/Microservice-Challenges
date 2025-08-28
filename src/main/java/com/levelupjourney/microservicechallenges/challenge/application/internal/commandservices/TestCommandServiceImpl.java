package com.levelupjourney.microservicechallenges.challenge.application.internal.commandservices;

import com.levelupjourney.microservicechallenges.challenge.domain.model.aggregates.Test;
import com.levelupjourney.microservicechallenges.challenge.domain.model.commands.AddTestToChallengeCommand;
import com.levelupjourney.microservicechallenges.challenge.domain.model.commands.RemoveTestFromChallengeCommand;
import com.levelupjourney.microservicechallenges.challenge.domain.model.commands.UpdateTestCommand;
import com.levelupjourney.microservicechallenges.challenge.domain.model.valueobjects.ChallengeState;
import com.levelupjourney.microservicechallenges.challenge.domain.services.TestCommandService;
import com.levelupjourney.microservicechallenges.challenge.infrastructure.persistence.jpa.repository.TestRepository;
import com.levelupjourney.microservicechallenges.challenge.infrastructure.persistence.jpa.repository.ChallengeVersionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class TestCommandServiceImpl implements TestCommandService {

    private final TestRepository testRepository;
    private final ChallengeVersionRepository challengeVersionRepository;

    public TestCommandServiceImpl(TestRepository testRepository, 
                                ChallengeVersionRepository challengeVersionRepository) {
        this.testRepository = testRepository;
        this.challengeVersionRepository = challengeVersionRepository;
    }

    @Override
    public Optional<Test> handle(AddTestToChallengeCommand command) {
        if (command == null) throw new IllegalArgumentException("Command cannot be null");
        
        // 1. Find challenge version by ID
        var challengeVersion = challengeVersionRepository.findById(command.challengeVersionId())
                .orElseThrow(() -> new IllegalArgumentException("Challenge version not found"));
        
        // 2. Validate challenge state
        if (challengeVersion.getChallenge().getState() != ChallengeState.DRAFT) {
            throw new IllegalStateException("Cannot add test to non-draft challenge");
        }
        
        // 3. Create new Test entity
        var test = new Test(
                command.title(),
                command.hint(),
                command.onErrorHint(),
                command.testCode(),
                command.input(),
                command.expectedOutput()
        );
        
        // 4. Associate test with challenge version
        challengeVersion.addTest(test);
        
        // 5. Save and return test
        var savedTest = testRepository.save(test);
        
        return Optional.of(savedTest);
    }

    @Override
    public void handle(RemoveTestFromChallengeCommand command) {
        if (command == null) throw new IllegalArgumentException("Command cannot be null");
        
        // 1. Find test by ID
        var test = testRepository.findById(command.testId())
                .orElseThrow(() -> new IllegalArgumentException("Test not found"));
        
        // 2. Validate test exists and belongs to correct challenge version
        var challengeVersion = test.getChallengeVersion();
        if (challengeVersion == null) {
            throw new IllegalStateException("Test is not associated with any challenge version");
        }
        
        var challenge = challengeVersion.getChallenge();
        if (challenge.getState() != ChallengeState.DRAFT) {
            throw new IllegalStateException("Cannot remove test from non-draft challenge");
        }
        
        // 3. Remove test from challenge version
        challengeVersion.removeTest(test);
        
        // 4. Delete test entity
        testRepository.delete(test);
    }

    @Override
    public Optional<Test> handle(UpdateTestCommand command) {
        if (command == null) throw new IllegalArgumentException("Command cannot be null");
        
        // 1. Find existing test by ID
        var test = testRepository.findById(command.testId())
                .orElseThrow(() -> new IllegalArgumentException("Test not found"));
        
        // 2. Validate challenge state
        var challenge = test.getChallengeVersion().getChallenge();
        if (challenge.getState() != ChallengeState.DRAFT) {
            throw new IllegalStateException("Cannot update test in non-draft challenge");
        }
        
        // 3. Update test properties with validation
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
        
        // 4. Save and return updated test
        var savedTest = testRepository.save(test);
        
        return Optional.of(savedTest);
    }
}
