package com.levelupjourney.microservicechallenges.solutions.interfaces.rest;

import com.levelupjourney.microservicechallenges.challenges.domain.model.queries.GetCodeVersionByIdQuery;
import com.levelupjourney.microservicechallenges.challenges.domain.services.CodeVersionQueryService;
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
import com.levelupjourney.microservicechallenges.solutions.interfaces.rest.resources.ErrorResponse;
import com.levelupjourney.microservicechallenges.solutions.interfaces.rest.transform.*;
import com.levelupjourney.microservicechallenges.shared.infrastructure.security.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = "/api/v1", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Solutions", description = "Endpoints for managing student solutions and challenge attempts")
public class SolutionController {

    private final SolutionCommandService solutionCommandService;
    private final SolutionQueryService solutionQueryService;
    private final CodeVersionQueryService codeVersionQueryService;
    private final JwtUtil jwtUtil;

    public SolutionController(SolutionCommandService solutionCommandService,
                              SolutionQueryService solutionQueryService,
                              CodeVersionQueryService codeVersionQueryService,
                              JwtUtil jwtUtil) {
        this.solutionCommandService = solutionCommandService;
        this.solutionQueryService = solutionQueryService;
        this.codeVersionQueryService = codeVersionQueryService;
        this.jwtUtil = jwtUtil;
    }

    // Create a new solution for a challenge's code version
    // Automatically initializes with code version's default code
    // POST /api/v1/challenges/{challengeId}/code-versions/{codeVersionId}/solutions
    @PostMapping("/challenges/{challengeId}/code-versions/{codeVersionId}/solutions")
    @Operation(summary = "Create solution", 
               description = "Create a new solution for a challenge. Automatically initializes with the code version's default code. Student can only have one solution per code version.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Solution created successfully with default code"),
        @ApiResponse(responseCode = "409", description = "Conflict - student already has a solution for this code version"),
        @ApiResponse(responseCode = "404", description = "Challenge or code version not found"),
        @ApiResponse(responseCode = "400", description = "Invalid challenge or code version ID")
    })
    public ResponseEntity<?> createSolution(
            @PathVariable String challengeId,
            @PathVariable String codeVersionId,
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        try {
            // Extract studentId from JWT token
            String studentId = jwtUtil.extractUserId(authorizationHeader);
            
            // Check if solution already exists for this student and code version
            var existingQuery = new GetSolutionByChallengeIdAndCodeVersionIdAndStudentIdQuery(
                    new ChallengeId(UUID.fromString(challengeId)),
                    new CodeVersionId(UUID.fromString(codeVersionId)),
                    new StudentId(UUID.fromString(studentId))
            );
            
            var existingSolution = solutionQueryService.handle(existingQuery);
            if (existingSolution.isPresent()) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new ErrorResponse("You already have a solution for this challenge code version. Use PUT to update it."));
            }
            
            // Get code version to retrieve default code
            var codeVersionQuery = new GetCodeVersionByIdQuery(
                    new com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.CodeVersionId(
                            UUID.fromString(codeVersionId)
                    )
            );
            var codeVersionOpt = codeVersionQueryService.handle(codeVersionQuery);
            
            if (codeVersionOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ErrorResponse("Code version not found with id: " + codeVersionId));
            }
            
            var codeVersion = codeVersionOpt.get();
            String defaultCode = codeVersion.getInitialCode() != null ? codeVersion.getInitialCode() : "";
            
            // Create solution resource with default code
            var resource = new CreateSolutionResource(defaultCode);
            
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

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to create solution"));
                    
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Invalid ID format: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Internal server error: " + e.getMessage()));
        }
    }

    // Get solution by ID
    // GET /api/v1/solutions/{solutionId}
    @GetMapping("/solutions/{solutionId}")
    @Operation(summary = "Get solution by ID", 
               description = "Retrieve a specific solution by its unique identifier")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Solution found"),
        @ApiResponse(responseCode = "404", description = "Solution not found"),
        @ApiResponse(responseCode = "400", description = "Invalid solution ID format")
    })
    public ResponseEntity<?> getSolutionById(@PathVariable String solutionId) {
        try {
            // Transform path variable to domain query
            var query = new GetSolutionByIdQuery(new SolutionId(UUID.fromString(solutionId)));

            // Execute query through domain service
            var solution = solutionQueryService.handle(query);

            // Transform domain entity to response resource if found
            if (solution.isPresent()) {
                var solutionResource = SolutionResourceFromEntityAssembler.toResourceFromEntity(solution.get());
                return new ResponseEntity<>(solutionResource, HttpStatus.OK);
            }

            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Solution not found with id: " + solutionId));
                    
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Invalid ID format: " + e.getMessage()));
        }
    }

    // Get solution by challenge, code version and student (from token or path)
    // GET /api/v1/challenges/{challengeId}/code-versions/{codeVersionId}/solutions
    @GetMapping("/challenges/{challengeId}/code-versions/{codeVersionId}/solutions")
    @Operation(summary = "Get student's solution", 
               description = "Retrieve the authenticated student's solution for a specific code version")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Solution found"),
        @ApiResponse(responseCode = "404", description = "Solution not found"),
        @ApiResponse(responseCode = "400", description = "Invalid ID format")
    })
    public ResponseEntity<?> getSolutionByContext(
            @PathVariable String challengeId,
            @PathVariable String codeVersionId,
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader) {

        try {
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

            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Solution not found for this challenge and code version"));
                    
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Invalid ID format: " + e.getMessage()));
        }
    }

    // Get specific student's solution (for teachers/admins)
    // GET /api/v1/students/{studentId}/code-versions/{codeVersionId}/solutions
    @GetMapping("/students/{studentId}/code-versions/{codeVersionId}/solutions")
    @Operation(summary = "Get specific student's solution", 
               description = "Retrieve a solution for a specific student and code version (for teachers/admins)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Solution found"),
        @ApiResponse(responseCode = "404", description = "Solution not found"),
        @ApiResponse(responseCode = "400", description = "Invalid ID format")
    })
    public ResponseEntity<?> getStudentSolution(
            @PathVariable String studentId,
            @PathVariable String codeVersionId) {

        try {
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

            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Solution not found for student with id: " + studentId));
                    
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Invalid ID format: " + e.getMessage()));
        }
    }

    // Update a solution's code
    // PUT /api/v1/solutions/{solutionId}
    @PutMapping("/solutions/{solutionId}")
    @Operation(summary = "Update solution code", 
               description = "Update only the student's code in an existing solution. Other solution properties cannot be modified.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Solution code updated successfully"),
        @ApiResponse(responseCode = "404", description = "Solution not found"),
        @ApiResponse(responseCode = "400", description = "Invalid request or ID format")
    })
    public ResponseEntity<?> updateSolution(
            @PathVariable String solutionId,
            @RequestBody UpdateSolutionResource resource) {
        try {
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

            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Solution not found with id: " + solutionId));
                    
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Invalid ID format: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to update solution: " + e.getMessage()));
        }
    }

    // Submit a solution for evaluation (RESTful: PUT to update status/state)
    // PUT /api/v1/solutions/{solutionId}/submissions
    @PutMapping("/solutions/{solutionId}/submissions")
    @Operation(summary = "Submit solution for evaluation", 
               description = "Submit a solution to be evaluated by the code runner service")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Solution submitted and evaluated successfully"),
        @ApiResponse(responseCode = "404", description = "Solution not found"),
        @ApiResponse(responseCode = "400", description = "Invalid request or ID format"),
        @ApiResponse(responseCode = "500", description = "Evaluation failed")
    })
    public ResponseEntity<?> submitSolution(
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
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Invalid ID format: " + e.getMessage()));
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