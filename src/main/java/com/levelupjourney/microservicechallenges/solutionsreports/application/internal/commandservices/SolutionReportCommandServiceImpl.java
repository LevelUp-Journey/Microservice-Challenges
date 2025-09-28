package com.levelupjourney.microservicechallenges.solutionsreports.application.internal.commandservices;

import com.levelupjourney.microservicechallenges.solutionsreports.domain.model.aggregates.SolutionReport;
import com.levelupjourney.microservicechallenges.solutionsreports.domain.model.commands.CreateSolutionReportCommand;
import com.levelupjourney.microservicechallenges.solutionsreports.domain.model.commands.DeleteSolutionReportBySolutionIdCommand;
import com.levelupjourney.microservicechallenges.solutionsreports.domain.model.commands.DeleteSolutionReportCommand;
import com.levelupjourney.microservicechallenges.solutionsreports.domain.model.commands.DeleteSolutionReportsByStudentIdCommand;
import com.levelupjourney.microservicechallenges.solutionsreports.domain.services.SolutionReportCommandService;
import com.levelupjourney.microservicechallenges.solutionsreports.infrastructure.persistence.jpa.repositories.SolutionReportRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class SolutionReportCommandServiceImpl implements SolutionReportCommandService {
    
    private final SolutionReportRepository solutionReportRepository;
    
    public SolutionReportCommandServiceImpl(SolutionReportRepository solutionReportRepository) {
        this.solutionReportRepository = solutionReportRepository;
    }
    
    @Override
    @Transactional
    public Optional<SolutionReport> handle(CreateSolutionReportCommand command) {
        try {
            // Create new solution report from command
            var solutionReport = new SolutionReport(command);
            
            // Save the solution report
            var savedSolutionReport = solutionReportRepository.save(solutionReport);
            
            return Optional.of(savedSolutionReport);
            
        } catch (Exception e) {
            // Log error and return empty optional
            System.err.println("Error creating solution report: " + e.getMessage());
            return Optional.empty();
        }
    }
    
    @Override
    @Transactional
    public boolean handle(DeleteSolutionReportBySolutionIdCommand command) {
        try {
            // Check if solution report exists
            var exists = solutionReportRepository.existsBySolutionId(command.solutionId().id());
            
            if (exists) {
                // Delete the solution report by solution ID
                solutionReportRepository.deleteBySolutionId(command.solutionId().id());
                return true;
            }
            
            return false;
            
        } catch (Exception e) {
            // Log error and return false
            System.err.println("Error deleting solution report by solution ID: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    @Transactional
    public boolean handle(DeleteSolutionReportCommand command) {
        try {
            // Check if solution report exists
            var exists = solutionReportRepository.existsById(command.reportId());
            
            if (exists) {
                // Delete the solution report by ID
                solutionReportRepository.deleteById(command.reportId());
                return true;
            }
            
            return false;
            
        } catch (Exception e) {
            // Log error and return false
            System.err.println("Error deleting solution report: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    @Transactional
    public int handle(DeleteSolutionReportsByStudentIdCommand command) {
        try {
            // Get all reports by student ID to count them
            var reports = solutionReportRepository.findByStudentId(command.studentId().id());
            int count = reports.size();
            
            if (count > 0) {
                // Delete all reports for the student
                reports.forEach(report -> solutionReportRepository.delete(report));
            }
            
            return count;
            
        } catch (Exception e) {
            // Log error and return 0
            System.err.println("Error deleting solution reports by student ID: " + e.getMessage());
            return 0;
        }
    }
}
