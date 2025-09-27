package com.levelupjourney.microservicechallenges.solutionsreports.application.internal.queryservices;

import com.levelupjourney.microservicechallenges.solutionsreports.domain.model.aggregates.SolutionReport;
import com.levelupjourney.microservicechallenges.solutionsreports.domain.model.queries.GetReportsBySolutionIdQuery;
import com.levelupjourney.microservicechallenges.solutionsreports.domain.model.queries.GetReportsByStudentIdQuery;
import com.levelupjourney.microservicechallenges.solutionsreports.domain.model.queries.GetSolutionReportByIdQuery;
import com.levelupjourney.microservicechallenges.solutionsreports.domain.services.SolutionReportQueryService;
import com.levelupjourney.microservicechallenges.solutionsreports.infrastructure.persistence.jpa.repositories.SolutionReportRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SolutionReportQueryServiceImpl implements SolutionReportQueryService {
    
    private final SolutionReportRepository solutionReportRepository;
    
    public SolutionReportQueryServiceImpl(SolutionReportRepository solutionReportRepository) {
        this.solutionReportRepository = solutionReportRepository;
    }
    
    @Override
    public Optional<SolutionReport> handle(GetReportsBySolutionIdQuery query) {
        try {
            // Find solution report by solution ID
            return solutionReportRepository.findBySolutionId(query.solutionId().value());
            
        } catch (Exception e) {
            // Log error and return empty optional
            System.err.println("Error finding solution report: " + e.getMessage());
            return Optional.empty();
        }
    }
    
    @Override
    public List<SolutionReport> handle(GetReportsByStudentIdQuery query) {
        try {
            // Find all solution reports by student ID
            return solutionReportRepository.findByStudentId(query.studentId().value());
            
        } catch (Exception e) {
            // Log error and return empty list
            System.err.println("Error finding solution reports by student: " + e.getMessage());
            return List.of();
        }
    }
    
    @Override
    public Optional<SolutionReport> handle(GetSolutionReportByIdQuery query) {
        try {
            // Find solution report by ID
            return solutionReportRepository.findById(query.reportId());
            
        } catch (Exception e) {
            // Log error and return empty optional
            System.err.println("Error finding solution report by ID: " + e.getMessage());
            return Optional.empty();
        }
    }
}
