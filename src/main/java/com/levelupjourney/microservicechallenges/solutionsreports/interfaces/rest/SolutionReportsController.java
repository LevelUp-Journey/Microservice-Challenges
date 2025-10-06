package com.levelupjourney.microservicechallenges.solutionsreports.interfaces.rest;

import com.levelupjourney.microservicechallenges.solutionsreports.domain.services.SolutionReportCommandService;
import com.levelupjourney.microservicechallenges.solutionsreports.domain.services.SolutionReportQueryService;
import com.levelupjourney.microservicechallenges.solutionsreports.interfaces.rest.resources.CreateSolutionReportResource;
import com.levelupjourney.microservicechallenges.solutionsreports.interfaces.rest.resources.SolutionReportResource;
import com.levelupjourney.microservicechallenges.solutionsreports.interfaces.rest.transform.CreateSolutionReportCommandFromResourceAssembler;
import com.levelupjourney.microservicechallenges.solutionsreports.interfaces.rest.transform.DeleteSolutionReportBySolutionIdCommandFromResourceAssembler;
import com.levelupjourney.microservicechallenges.solutionsreports.interfaces.rest.transform.GetReportsBySolutionIdQueryFromResourceAssembler;
import com.levelupjourney.microservicechallenges.solutionsreports.interfaces.rest.transform.GetReportsByStudentIdQueryFromResourceAssembler;
import com.levelupjourney.microservicechallenges.solutionsreports.interfaces.rest.transform.GetSolutionReportByIdQueryFromResourceAssembler;
import com.levelupjourney.microservicechallenges.solutionsreports.interfaces.rest.transform.SolutionReportResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/v1/solution-reports", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Solution Reports", description = "Endpoints for managing solution reports")
public class SolutionReportsController {
    
    private final SolutionReportCommandService solutionReportCommandService;
    private final SolutionReportQueryService solutionReportQueryService;
    
    public SolutionReportsController(SolutionReportCommandService solutionReportCommandService,
                                   SolutionReportQueryService solutionReportQueryService) {
        this.solutionReportCommandService = solutionReportCommandService;
        this.solutionReportQueryService = solutionReportQueryService;
    }
    
    // POST /api/v1/solution-reports - Create a new solution report
    @PostMapping
    @Operation(summary = "Create a new solution report", description = "Creates a new solution report with execution results")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Solution report created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<SolutionReportResource> createSolutionReport(@RequestBody CreateSolutionReportResource resource) {
        // Transform resource to domain command
        var command = CreateSolutionReportCommandFromResourceAssembler.toCommandFromResource(resource);
        
        // Execute command through domain service
        var solutionReport = solutionReportCommandService.handle(command);
        
        // Transform domain entity to response resource
        if (solutionReport.isPresent()) {
            var reportResource = SolutionReportResourceFromEntityAssembler.toResourceFromEntity(solutionReport.get());
            return new ResponseEntity<>(reportResource, HttpStatus.CREATED);
        }
        
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    // GET /api/v1/solution-reports/solution/{solutionId} - Get report by solution ID
    @GetMapping("/solution/{solutionId}")
    @Operation(summary = "Get solution report by solution ID", description = "Retrieves the solution report for a specific solution")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Solution report found"),
            @ApiResponse(responseCode = "404", description = "Solution report not found"),
            @ApiResponse(responseCode = "400", description = "Invalid solution ID format")
    })
    public ResponseEntity<SolutionReportResource> getSolutionReportBySolutionId(@PathVariable String solutionId) {
        try {
            // Transform path variable to domain query
            var query = GetReportsBySolutionIdQueryFromResourceAssembler.toQueryFromSolutionId(solutionId);
            
            // Execute query through domain service
            var solutionReport = solutionReportQueryService.handle(query);
            
            // Transform domain entity to response resource if found
            if (solutionReport.isPresent()) {
                var reportResource = SolutionReportResourceFromEntityAssembler.toResourceFromEntity(solutionReport.get());
                return new ResponseEntity<>(reportResource, HttpStatus.OK);
            }
            
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    // GET /api/v1/solution-reports/student/{studentId} - Get all reports by student ID
    @GetMapping("/students/{studentId}")
    @Operation(summary = "Get solution reports by student ID", description = "Retrieves all solution reports for a specific student")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Solution reports found"),
            @ApiResponse(responseCode = "400", description = "Invalid student ID format"),
            @ApiResponse(responseCode = "404", description = "No reports found for student")
    })
    public ResponseEntity<java.util.List<SolutionReportResource>> getSolutionReportsByStudentId(@PathVariable String studentId) {
        try {
            // Transform path variable to domain query
            var query = GetReportsByStudentIdQueryFromResourceAssembler.toQueryFromStudentId(studentId);
            
            // Execute query through domain service
            var solutionReports = solutionReportQueryService.handle(query);
            
            // Transform domain entities to response resources
            var reportResources = solutionReports.stream()
                    .map(SolutionReportResourceFromEntityAssembler::toResourceFromEntity)
                    .collect(java.util.stream.Collectors.toList());
                    
            return new ResponseEntity<>(reportResources, HttpStatus.OK);
            
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    // GET /api/v1/solution-reports/{reportId} - Get report by ID
    @GetMapping("/{reportId}")
    @Operation(summary = "Get solution report by ID", description = "Retrieves a specific solution report by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Solution report found"),
            @ApiResponse(responseCode = "404", description = "Solution report not found"),
            @ApiResponse(responseCode = "400", description = "Invalid report ID format")
    })
    public ResponseEntity<SolutionReportResource> getSolutionReportById(@PathVariable String reportId) {
        try {
            // Transform path variable to domain query
            var query = GetSolutionReportByIdQueryFromResourceAssembler.toQueryFromReportId(reportId);
            
            // Execute query through domain service
            var solutionReport = solutionReportQueryService.handle(query);
            
            // Transform domain entity to response resource if found
            if (solutionReport.isPresent()) {
                var reportResource = SolutionReportResourceFromEntityAssembler.toResourceFromEntity(solutionReport.get());
                return new ResponseEntity<>(reportResource, HttpStatus.OK);
            }
            
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    // DELETE /api/v1/solution-reports/solution/{solutionId} - Delete report by solution ID
    @DeleteMapping("/solution/{solutionId}")
    @Operation(summary = "Delete solution report by solution ID", description = "Deletes the solution report for a specific solution")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Solution report deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Solution report not found"),
            @ApiResponse(responseCode = "400", description = "Invalid solution ID format")
    })
    public ResponseEntity<Void> deleteSolutionReportBySolutionId(@PathVariable String solutionId) {
        try {
            // Transform path variable to domain command
            var command = DeleteSolutionReportBySolutionIdCommandFromResourceAssembler.toCommandFromSolutionId(solutionId);
            
            // Execute command through domain service
            boolean deleted = solutionReportCommandService.handle(command);
            
            if (deleted) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    // DELETE /api/v1/solution-reports/{reportId} - Delete report by ID
    @DeleteMapping("/{reportId}")
    @Operation(summary = "Delete solution report by ID", description = "Deletes a specific solution report by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Solution report deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Solution report not found"),
            @ApiResponse(responseCode = "400", description = "Invalid report ID format")
    })
    public ResponseEntity<Void> deleteSolutionReportById(@PathVariable String reportId) {
        try {
            // Transform path variable to domain command
            var command = com.levelupjourney.microservicechallenges.solutionsreports.interfaces.rest.transform.DeleteSolutionReportCommandFromResourceAssembler.toCommandFromReportId(reportId);
            
            // Execute command through domain service
            boolean deleted = solutionReportCommandService.handle(command);
            
            if (deleted) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
