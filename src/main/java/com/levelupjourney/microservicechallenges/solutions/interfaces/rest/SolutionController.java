package com.levelupjourney.microservicechallenges.solutions.interfaces.rest;

import com.levelupjourney.microservicechallenges.solutions.domain.model.queries.GetSolutionByIdQuery;
import com.levelupjourney.microservicechallenges.solutions.domain.model.queries.GetSolutionByStudentIdAndCodeVersionIdQuery;
import com.levelupjourney.microservicechallenges.solutions.domain.model.valueobjects.CodeVersionId;
import com.levelupjourney.microservicechallenges.solutions.domain.model.valueobjects.SolutionId;
import com.levelupjourney.microservicechallenges.solutions.domain.model.valueobjects.StudentId;
import com.levelupjourney.microservicechallenges.solutions.domain.services.SolutionCommandService;
import com.levelupjourney.microservicechallenges.solutions.domain.services.SolutionQueryService;
import com.levelupjourney.microservicechallenges.solutions.interfaces.rest.resource.*;
import com.levelupjourney.microservicechallenges.solutions.interfaces.rest.transform.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(value = "/api/v1/solutions", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Solutions", description = "Endpoints for managing student solutions")
public class SolutionController {

    private final SolutionCommandService solutionCommandService;
    private final SolutionQueryService solutionQueryService;

    public SolutionController(SolutionCommandService solutionCommandService,
                             SolutionQueryService solutionQueryService) {
        this.solutionCommandService = solutionCommandService;
        this.solutionQueryService = solutionQueryService;
    }

    // Create a new solution
    @PostMapping
    public ResponseEntity<SolutionResource> createSolution(@RequestBody CreateSolutionResource resource) {
        // Transform resource to domain command
        var command = CreateSolutionCommandFromResourceAssembler.toCommandFromResource(resource);

        // Execute command through domain service
        var solution = solutionCommandService.handle(command);

        // Transform domain entity to response resource
        if (solution.isPresent()) {
            var solutionResource = SolutionResourceFromEntityAssembler.toResourceFromEntity(solution.get());
            return new ResponseEntity<>(solutionResource, HttpStatus.CREATED);
        }

        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Get solution by ID
    @GetMapping("/{solutionId}")
    public ResponseEntity<SolutionResource> getSolutionById(@PathVariable String solutionId) {
        // Transform path variable to domain query
        var query = new GetSolutionByIdQuery(new SolutionId(UUID.fromString(solutionId)));

        // Execute query through domain service
        var solution = solutionQueryService.handle(query);

        // Transform domain entity to response resource if found
        if (solution.isPresent()) {
            var solutionResource = SolutionResourceFromEntityAssembler.toResourceFromEntity(solution.get());
            return new ResponseEntity<>(solutionResource, HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // Get solution by student ID and code version ID
    @GetMapping("/student/{studentId}/code-version/{codeVersionId}")
    public ResponseEntity<SolutionResource> getSolutionByStudentAndCodeVersion(
            @PathVariable String studentId,
            @PathVariable String codeVersionId) {
        
        // Transform path variables to domain query
        var query = new GetSolutionByStudentIdAndCodeVersionIdQuery(
            new StudentId(UUID.fromString(studentId)),
            new CodeVersionId(UUID.fromString(codeVersionId))
        );

        // Execute query through domain service
        var solution = solutionQueryService.handle(query);

        // Transform domain entity to response resource if found
        if (solution.isPresent()) {
            var solutionResource = SolutionResourceFromEntityAssembler.toResourceFromEntity(solution.get());
            return new ResponseEntity<>(solutionResource, HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // Update a solution
    @PutMapping("/{solutionId}")
    public ResponseEntity<SolutionResource> updateSolution(@PathVariable String solutionId,
                                                          @RequestBody UpdateSolutionResource resource) {
        // Transform resource to domain command
        var command = UpdateSolutionCommandFromResourceAssembler.toCommandFromResource(solutionId, resource);

        // Execute command through domain service
        solutionCommandService.handle(command);

        // Retrieve updated solution for response
        var query = new GetSolutionByIdQuery(new SolutionId(UUID.fromString(solutionId)));
        var solution = solutionQueryService.handle(query);

        // Transform domain entity to response resource
        if (solution.isPresent()) {
            var solutionResource = SolutionResourceFromEntityAssembler.toResourceFromEntity(solution.get());
            return new ResponseEntity<>(solutionResource, HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // Submit a solution for evaluation
    @PostMapping("/{solutionId}/submit")
    public ResponseEntity<Void> submitSolution(@PathVariable String solutionId,
                                              @RequestBody SubmitSolutionResource resource) {
        // Transform resource to domain command
        var command = SubmitSolutionCommandFromResourceAssembler.toCommandFromResource(solutionId, resource);

        // Execute command through domain service
        var solutionReportId = solutionCommandService.handle(command);

        // Return success response with the report ID in headers for reference
        return ResponseEntity.ok()
                .header("Solution-Report-Id", solutionReportId.value().toString())
                .build();
    }
}