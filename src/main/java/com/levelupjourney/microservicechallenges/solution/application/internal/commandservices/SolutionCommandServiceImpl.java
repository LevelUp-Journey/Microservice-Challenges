package com.levelupjourney.microservicechallenges.solution.application.internal.commandservices;

import com.levelupjourney.microservicechallenges.solution.domain.model.aggregates.Solution;
import com.levelupjourney.microservicechallenges.solution.domain.model.commands.CreateSolutionCommand;
import com.levelupjourney.microservicechallenges.solution.domain.model.commands.SubmitSolutionCommand;
import com.levelupjourney.microservicechallenges.solution.domain.model.commands.UpdateSolutionCommand;
import com.levelupjourney.microservicechallenges.solution.domain.services.SolutionCommandService;
import com.levelupjourney.microservicechallenges.solution.infrastructure.persistence.jpa.repository.SolutionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class SolutionCommandServiceImpl implements SolutionCommandService {

    private final SolutionRepository solutionRepository;

    public SolutionCommandServiceImpl(SolutionRepository solutionRepository) {
        this.solutionRepository = solutionRepository;
    }

    @Override
    public Optional<Solution> handle(CreateSolutionCommand command) {
        if (command == null) throw new IllegalArgumentException("Command cannot be null");
        
        // 1. Check if solution already exists for this student and challenge
        var existingSolution = solutionRepository.findByStudentIdAndChallengeId(
                command.studentId(), 
                command.challengeId()
        );
        
        if (existingSolution.isPresent()) {
            throw new IllegalStateException("Solution already exists for this student and challenge");
        }
        
        // 2. Create new Solution aggregate
        var solution = new Solution(
                command.studentId(),
                command.challengeId(),
                command.language(),
                command.code()
        );
        
        // 3. Save solution
        var savedSolution = solutionRepository.save(solution);
        
        return Optional.of(savedSolution);
    }

    @Override
    public Optional<Solution> handle(SubmitSolutionCommand command) {
        if (command == null) throw new IllegalArgumentException("Command cannot be null");
        
        // 1. Find existing solution or create new one
        var existingSolution = solutionRepository.findByStudentIdAndChallengeId(
                command.studentId(), 
                command.challengeId()
        );
        
        Solution solution;
        if (existingSolution.isPresent()) {
            // Update existing solution
            solution = existingSolution.get();
            solution.updateCode(command.code());
            solution.updateLanguage(command.language());
        } else {
            // Create new solution
            solution = new Solution(
                    command.studentId(),
                    command.challengeId(),
                    command.language(),
                    command.code()
            );
        }
        
        // 2. TODO: Run tests against the solution
        // This is where we would integrate with the Code Runner Microservice
        // For now, we'll just reset the passed tests
        solution.updatePassedTests(List.of());
        
        // 3. Save and return solution
        var savedSolution = solutionRepository.save(solution);
        
        return Optional.of(savedSolution);
    }

    @Override
    public Optional<Solution> handle(UpdateSolutionCommand command) {
        if (command == null) throw new IllegalArgumentException("Command cannot be null");
        
        // 1. Find existing solution by ID
        var solution = solutionRepository.findById(command.solutionId())
                .orElseThrow(() -> new IllegalArgumentException("Solution not found"));
        
        // 2. Update solution properties with validation
        boolean codeChanged = false;
        boolean languageChanged = false;
        
        if (command.code() != null && !command.code().trim().isEmpty() && 
            !command.code().equals(solution.getCode())) {
            solution.updateCode(command.code());
            codeChanged = true;
        }
        
        if (command.language() != null && !command.language().equals(solution.getLanguage())) {
            solution.updateLanguage(command.language());
            languageChanged = true;
        }
        
        // 3. If code or language changed, reset passed tests
        if (codeChanged || languageChanged) {
            solution.updatePassedTests(List.of());
        }
        
        // 4. Save and return updated solution
        var savedSolution = solutionRepository.save(solution);
        
        return Optional.of(savedSolution);
    }
}
