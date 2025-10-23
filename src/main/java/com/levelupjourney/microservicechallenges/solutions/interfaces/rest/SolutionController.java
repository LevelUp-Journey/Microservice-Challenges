package com.levelupjourney.microservicechallenges.solutions.interfaces.rest;

import com.levelupjourney.microservicechallenges.solutions.domain.model.queries.GetSolutionByChallengeIdAndCodeVersionIdAndStudentIdQuery;
import com.levelupjourney.microservicechallenges.solutions.domain.model.queries.GetSolutionByIdQuery;
import com.levelupjourney.microservicechallenges.solutions.domain.model.queries.GetSolutionByStudentIdAndCodeVersionIdQuery;
import com.levelupjourney.microservicechallenges.solutions.domain.model.valueobjects.ChallengeId;
import com.levelupjourney.microservicechallenges.solutions.domain.model.valueobjects.CodeVersionId;
import com.levelupjourney.microservicechallenges.solutions.domain.model.valueobjects.SolutionId;
import com.levelupjourney.microservicechallenges.solutions.domain.model.valueobjects.StudentId;
import com.levelupjourney.microservicechallenges.solutions.domain.services.SolutionCommandService;
import com.levelupjourney.microservicechallenges.solutions.domain.services.SolutionQueryService;
import com.levelupjourney.microservicechallenges.solutions.interfaces.rest.resource.*;
import com.levelupjourney.microservicechallenges.solutions.interfaces.rest.transform.*;
import com.levelupjourney.microservicechallenges.shared.infrastructure.security.JwtUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = "/api/v1", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Solutions", description = "Endpoints for managing student solutions")
public class SolutionController {

    private final SolutionCommandService solutionCommandService;
    private final SolutionQueryService solutionQueryService;
    private final JwtUtil jwtUtil;

    public SolutionController(SolutionCommandService solutionCommandService,
                              SolutionQueryService solutionQueryService,
                              JwtUtil jwtUtil) {
        this.solutionCommandService = solutionCommandService;
        this.solutionQueryService = solutionQueryService;
        this.jwtUtil = jwtUtil;
    }

    // Create a new solution for a challenge's code version
    // POST /api/v1/challenges/{challengeId}/code-versions/{codeVersionId}/solutions
    @PostMapping("/challenges/{challengeId}/code-versions/{codeVersionId}/solutions")
    public ResponseEntity<SolutionResource> createSolution(
            @PathVariable String challengeId,
            @PathVariable String codeVersionId,
            @RequestBody CreateSolutionResource resource,
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        // Extract studentId from JWT token
        String studentId = jwtUtil.extractUserId(authorizationHeader);
        
        // Transform resource to domain command with IDs from path
        var command = CreateSolutionCommandFromResourceAssembler.toCommandFromResource(
            challengeId, codeVersionId, resource, studentId);

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
    // GET /api/v1/solutions/{solutionId}
    @GetMapping("/solutions/{solutionId}")
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

    // Get solution by challenge, code version and student (from token or path)
    // GET /api/v1/challenges/{challengeId}/code-versions/{codeVersionId}/solutions
    @GetMapping("/challenges/{challengeId}/code-versions/{codeVersionId}/solutions")
    public ResponseEntity<SolutionResource> getSolutionByContext(
            @PathVariable String challengeId,
            @PathVariable String codeVersionId,
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader) {

        // Extract studentId from JWT token (student's own solution)
        String studentId = jwtUtil.extractUserId(authorizationHeader);

        // Transform path variables to domain query
        var query = new GetSolutionByChallengeIdAndCodeVersionIdAndStudentIdQuery(
                new ChallengeId(UUID.fromString(challengeId)),
                new CodeVersionId(UUID.fromString(codeVersionId)),
                new StudentId(UUID.fromString(studentId))
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

    // Get specific student's solution (for teachers/admins)
    // GET /api/v1/students/{studentId}/code-versions/{codeVersionId}/solutions
    @GetMapping("/students/{studentId}/code-versions/{codeVersionId}/solutions")
    public ResponseEntity<SolutionResource> getStudentSolution(
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
    // PUT /api/v1/solutions/{solutionId}
    @PutMapping("/solutions/{solutionId}")
    public ResponseEntity<SolutionResource> updateSolution(
            @PathVariable String solutionId,
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

    // Submit a solution for evaluation (RESTful: PUT to update status/state)
    // PUT /api/v1/solutions/{solutionId}/submissions
    @PutMapping("/solutions/{solutionId}/submissions")
    public ResponseEntity<SubmissionResultResource> submitSolution(
            @PathVariable String solutionId,
            @RequestBody SubmitSolutionResource resource,
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        try {
            // Extract studentId from JWT token
            String studentId = jwtUtil.extractUserId(authorizationHeader);
            
            // Transform resource to domain command with studentId from token
            var command = SubmitSolutionCommandFromResourceAssembler.toCommandFromResource(solutionId, resource, studentId);

            // Execute command through domain service
            var submissionResult = solutionCommandService.handle(command);

            // Transform domain result to resource
            var result = new SubmissionResultResource(
                    submissionResult.solutionReportId() != null ? 
                        submissionResult.solutionReportId().value().toString() : null,
                    submissionResult.message(),
                    submissionResult.success(),
                    submissionResult.approvedTestIds(),
                    submissionResult.totalTests(),
                    submissionResult.getPassedTests(),
                    submissionResult.executionDetails(),
                    submissionResult.timeTaken()
            );

            if (submissionResult.success()) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
            }
            
        } catch (Exception e) {
            var errorResult = new SubmissionResultResource(
                    null,
                    "Internal server error during submission: " + e.getMessage(),
                    false,
                    List.of(),
                    0,
                    0,
                    "Unexpected error occurred",
                    0.0
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResult);
        }
    }
}