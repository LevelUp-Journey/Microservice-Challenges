package com.levelupjourney.microservicechallenges.challenges.application.internal.commandservices;

import com.levelupjourney.microservicechallenges.challenges.domain.model.aggregates.CodeVersionTest;
import com.levelupjourney.microservicechallenges.challenges.domain.model.commands.AddCodeVersionTestCommand;
import com.levelupjourney.microservicechallenges.challenges.domain.model.commands.UpdateCodeVersionTestCommand;
import com.levelupjourney.microservicechallenges.challenges.domain.model.queries.GetChallengeByIdQuery;
import com.levelupjourney.microservicechallenges.challenges.domain.model.queries.GetCodeVersionByIdQuery;
import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.CodeVersionTestId;
import com.levelupjourney.microservicechallenges.challenges.domain.services.ChallengeQueryService;
import com.levelupjourney.microservicechallenges.challenges.domain.services.CodeVersionQueryService;
import com.levelupjourney.microservicechallenges.challenges.domain.services.CodeVersionTestCommandService;
import com.levelupjourney.microservicechallenges.challenges.infrastructure.persistence.jpa.repositories.CodeVersionTestRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class CodeVersionTestCommandServiceImpl implements CodeVersionTestCommandService {

    private final CodeVersionTestRepository codeVersionTestRepository;
    private final CodeVersionQueryService codeVersionQueryService;
    private final ChallengeQueryService challengeQueryService;
    
    public CodeVersionTestCommandServiceImpl(CodeVersionTestRepository codeVersionTestRepository,
                                           CodeVersionQueryService codeVersionQueryService,
                                           ChallengeQueryService challengeQueryService) {
        this.codeVersionTestRepository = codeVersionTestRepository;
        this.codeVersionQueryService = codeVersionQueryService;
        this.challengeQueryService = challengeQueryService;
    }

    @Override
    @Transactional
    public CodeVersionTestId handle(AddCodeVersionTestCommand command) {
        // Validate that the code version exists
        var codeVersionQuery = new GetCodeVersionByIdQuery(command.codeVersionId());
        var codeVersion = codeVersionQueryService.handle(codeVersionQuery);
        
        if (codeVersion.isEmpty()) {
            throw new IllegalArgumentException("Code version not found with ID: " + command.codeVersionId().id());
        }
        
        // Additionally validate that the challenge exists (extra integrity check)
        var challengeQuery = new GetChallengeByIdQuery(codeVersion.get().getChallengeId());
        var challenge = challengeQueryService.handle(challengeQuery);
        
        if (challenge.isEmpty()) {
            throw new IllegalArgumentException("Challenge not found with ID: " + codeVersion.get().getChallengeId().id() + 
                                             " (referenced by code version: " + command.codeVersionId().id() + ")");
        }
        
        // Create new test for code version using constructor
        CodeVersionTest codeVersionTest = new CodeVersionTest(command);
        
        // Save to database
        CodeVersionTest savedTest = codeVersionTestRepository.save(codeVersionTest);
        return savedTest.getId();
    }

    @Override
    @Transactional
    public void handle(UpdateCodeVersionTestCommand command) {
        // Find test by ID
        CodeVersionTest codeVersionTest = codeVersionTestRepository.findById(command.codeVersionTestId())
                .orElseThrow(() -> new IllegalArgumentException("Code version test not found: " + command.codeVersionTestId().id()));
        
        // Update test details using business method with Optional handling
        codeVersionTest.updateTestDetails(
            command.input().orElse(null),
            command.expectedOutput().orElse(null),
            command.customValidationCode().orElse(null),
            command.failureMessage().orElse(null),
            command.isSecret().orElse(null)
        );
        
        // Save changes
        codeVersionTestRepository.save(codeVersionTest);
    }
}
