package com.levelupjourney.microservicechallenges.challenges.application.internal.commandservices;

import com.levelupjourney.microservicechallenges.challenges.domain.model.aggregates.CodeVersion;
import com.levelupjourney.microservicechallenges.challenges.domain.model.commands.AddCodeVersionCommand;
import com.levelupjourney.microservicechallenges.challenges.domain.model.commands.UpdateCodeVersionCommand;
import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.CodeVersionId;
import com.levelupjourney.microservicechallenges.challenges.domain.services.CodeVersionCommandService;
import com.levelupjourney.microservicechallenges.challenges.infrastructure.persistence.jpa.repositories.CodeVersionRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class CodeVersionCommandServiceImpl implements CodeVersionCommandService {

    private final CodeVersionRepository codeVersionRepository;
    
    public CodeVersionCommandServiceImpl(CodeVersionRepository codeVersionRepository) {
        this.codeVersionRepository = codeVersionRepository;
    }

    @Override
    @Transactional
    public CodeVersionId handle(AddCodeVersionCommand command) {
        // Check if a version already exists for this challenge and language
        var existingVersion = codeVersionRepository
                .findByChallengeIdAndLanguage(command.challengeId().id(), command.language());
        
        if (existingVersion.isPresent()) {
            throw new RuntimeException("Code version already exists for challenge and language");
        }
        
        // Create new code version using constructor
        CodeVersion codeVersion = new CodeVersion(command);
        
        // Save to database
        CodeVersion savedCodeVersion = codeVersionRepository.save(codeVersion);
        return savedCodeVersion.getId();
    }

    @Override
    @Transactional
    public void handle(UpdateCodeVersionCommand command) {
        // Find code version by ID
        CodeVersion codeVersion = codeVersionRepository.findById(command.codeVersionId())
                .orElseThrow(() -> new RuntimeException("Code version not found: " + command.codeVersionId().id()));
        
        // Update code using business method
        codeVersion.updateInitialCode(command.code());
        
        // Save changes
        codeVersionRepository.save(codeVersion);
    }
}
