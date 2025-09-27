package com.levelupjourney.microservicechallenges.challenges.application.internal.commandservices;

import com.levelupjourney.microservicechallenges.challenges.domain.model.aggregates.CodeVersionTest;
import com.levelupjourney.microservicechallenges.challenges.domain.model.commands.AddCodeVersionTestCommand;
import com.levelupjourney.microservicechallenges.challenges.domain.model.commands.UpdateCodeVersionTestCommand;
import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.CodeVersionTestId;
import com.levelupjourney.microservicechallenges.challenges.domain.services.CodeVersionTestCommandService;
import com.levelupjourney.microservicechallenges.challenges.infrastructure.persistence.jpa.repositories.CodeVersionTestRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class CodeVersionTestCommandServiceImpl implements CodeVersionTestCommandService {

    private final CodeVersionTestRepository codeVersionTestRepository;
    
    public CodeVersionTestCommandServiceImpl(CodeVersionTestRepository codeVersionTestRepository) {
        this.codeVersionTestRepository = codeVersionTestRepository;
    }

    @Override
    @Transactional
    public CodeVersionTestId handle(AddCodeVersionTestCommand command) {
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
                .orElseThrow(() -> new RuntimeException("Code version test not found: " + command.codeVersionTestId().value()));
        
        // Update test details using business method with Optional handling
        codeVersionTest.updateTestDetails(
            command.input().orElse(null),
            command.expectedOutput().orElse(null),
            command.customValidationCode().orElse(null),
            command.failureMessage().orElse(null)
        );
        
        // Save changes
        codeVersionTestRepository.save(codeVersionTest);
    }
}
