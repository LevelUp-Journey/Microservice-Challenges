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
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
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
    @Operation(
        summary = "Create solution", 
        description = "Create a new solution for a challenge. Automatically initializes with the code version's default code. Student can only have one solution per code version. Requires JWT authentication."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201", 
            description = "Solution created successfully with default code",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = SolutionResource.class))
        ),
        @ApiResponse(
            responseCode = "409", 
            description = "Conflict - student already has a solution for this code version",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Challenge or code version not found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Invalid challenge or code version ID format",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Internal server error",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    public ResponseEntity<?> createSolution(
            @Parameter(description = "UUID of the challenge") @PathVariable String challengeId,
            @Parameter(description = "UUID of the code version") @PathVariable String codeVersionId,
            @Parameter(description = "JWT Bearer token") @RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
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
    @Operation(
        summary = "Get solution by ID", 
        description = "Retrieve a specific solution by its unique identifier. No authentication required."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Solution found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = SolutionResource.class))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Solution not found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Invalid solution ID format",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    public ResponseEntity<?> getSolutionById(
            @Parameter(description = "UUID of the solution") @PathVariable String solutionId) {
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

    // Get solution by challenge, code version and student (from token)
    // GET /api/v1/challenges/{challengeId}/code-versions/{codeVersionId}/solutions
    @GetMapping("/challenges/{challengeId}/code-versions/{codeVersionId}/solutions")
    @Operation(
        summary = "Get student's solution", 
        description = "Retrieve the authenticated student's solution for a specific code version. The student ID is extracted from the JWT token. Requires authentication."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Solution found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = SolutionResource.class))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Solution not found for this challenge and code version",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Invalid ID format",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "401", 
            description = "Unauthorized - JWT token required",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    public ResponseEntity<?> getSolutionByContext(
            @Parameter(description = "UUID of the challenge") @PathVariable String challengeId,
            @Parameter(description = "UUID of the code version") @PathVariable String codeVersionId,
            @Parameter(description = "JWT Bearer token") @RequestHeader(value = "Authorization", required = false) String authorizationHeader) {

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
    @Operation(
        summary = "Get specific student's solution", 
        description = "Retrieve a solution for a specific student and code version. Intended for teachers/admins to review student work. Requires appropriate authorization."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Solution found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = SolutionResource.class))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Solution not found for the specified student",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Invalid ID format",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "403", 
            description = "Forbidden - insufficient permissions",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    public ResponseEntity<?> getStudentSolution(
            @Parameter(description = "UUID of the student") @PathVariable String studentId,
            @Parameter(description = "UUID of the code version") @PathVariable String codeVersionId) {

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

    // Update a solution's code (CQRS Command)
    // PUT /api/v1/solutions/{solutionId}
    @PutMapping("/solutions/{solutionId}")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Solution code successfully updated. Returns the complete updated solution.",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = SolutionResource.class),
                examples = @ExampleObject(
                    name = "Updated Solution",
                    description = "Example of a successfully updated solution response",
                    value = """
                        {
                          "id": "550e8400-e29b-41d4-a716-446655440000",
                          "challengeId": "123e4567-e89b-12d3-a456-426614174000",
                          "codeVersionId": "789e0123-e45b-67c8-d901-234567890abc",
                          "studentId": "456e7890-e12b-34c5-d678-901234567def",
                          "code": "function solve(n) {\\n  if (n < 2) return false;\\n  for (let i = 2; i <= Math.sqrt(n); i++) {\\n    if (n % i === 0) return false;\\n  }\\n  return true;\\n}",
                          "status": "IN_PROGRESS",
                          "attempts": 0,
                          "lastAttemptAt": null,
                          "score": null,
                          "createdAt": "2024-01-15T10:30:00"
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Bad Request - Invalid input data or business rule violation",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = {
                    @ExampleObject(
                        name = "Invalid UUID Format",
                        description = "Solution ID is not a valid UUID",
                        value = """
                            {
                              "message": "Invalid solution ID format: 'invalid-uuid'. Expected a valid UUID.",
                              "timestamp": "2024-01-15T10:30:00"
                            }
                            """
                    ),
                    @ExampleObject(
                        name = "Empty Code",
                        description = "Code field is empty or contains only whitespace",
                        value = """
                            {
                              "message": "Code cannot be empty or contain only whitespace",
                              "timestamp": "2024-01-15T10:30:00"
                            }
                            """
                    ),
                    @ExampleObject(
                        name = "Code Too Long",
                        description = "Code exceeds maximum length",
                        value = """
                            {
                              "message": "Code cannot exceed 50000 characters (current: 51234)",
                              "timestamp": "2024-01-15T10:30:00"
                            }
                            """
                    )
                }
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Not Found - Solution with the specified ID does not exist",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(
                    name = "Solution Not Found",
                    description = "No solution exists with the provided ID",
                    value = """
                        {
                          "message": "Solution not found with ID: 550e8400-e29b-41d4-a716-446655440000",
                          "timestamp": "2024-01-15T10:30:00"
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal Server Error - Unexpected error during solution update",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(
                    name = "Internal Server Error",
                    description = "Unexpected error occurred while processing the request",
                    value = """
                        {
                          "message": "Failed to update solution: Database connection timeout",
                          "timestamp": "2024-01-15T10:30:00"
                        }
                        """
                )
            )
        )
    })
    public ResponseEntity<?> updateSolution(
            @Parameter(
                description = "Unique identifier of the solution to update (UUID format)",
                example = "550e8400-e29b-41d4-a716-446655440000",
                required = true
            ) 
            @PathVariable String solutionId,
            
            @RequestBody @jakarta.validation.Valid UpdateSolutionResource resource) {
        
        try {

            // Step 1: Transform REST resource to Domain command (Anti-Corruption Layer)
            var command = UpdateSolutionCommandFromResourceAssembler.toCommandFromResource(
                solutionId, 
                resource
            );

            // Step 2: Execute command through Application Service (Command Handler)
            solutionCommandService.handle(command);


            // Step 3: Retrieve updated solution for response
            var query = new GetSolutionByIdQuery(command.solutionId());
            var solutionOptional = solutionQueryService.handle(query);

            // Step 4: Transform domain entity to REST resource
            if (solutionOptional.isPresent()) {
                var solutionResource = SolutionResourceFromEntityAssembler.toResourceFromEntity(
                    solutionOptional.get()
                );
                return ResponseEntity.ok(solutionResource);
            }

            // This should never happen as the command handler validates existence
            // But we handle it defensively for robustness
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(
                    String.format("Solution not found with ID: %s", solutionId)
                ));
                    
        } catch (IllegalArgumentException e) {
            // Handle validation errors (invalid UUID, empty code, etc.)
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(e.getMessage()));
                
        } catch (IllegalStateException e) {
            // Handle domain state errors (aggregate invariant violations)
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(e.getMessage()));
                
        } catch (Exception e) {
            // Handle unexpected errors
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(
                    String.format("Failed to update solution: %s", e.getMessage())
                ));
        }
    }

    // Submit a solution for evaluation (RESTful: PUT to update status/state)
    // PUT /api/v1/solutions/{solutionId}/submissions
    @PutMapping("/solutions/{solutionId}/submissions")
    @Operation(
        summary = "Submit solution for evaluation", 
        description = "Submit a solution to be evaluated by the code runner service. The solution code is automatically retrieved from the database. Only the solution owner (student), teachers, or admins can submit solutions for evaluation."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Solution submitted and evaluated successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = SubmissionResultResource.class))
        ),
        @ApiResponse(
            responseCode = "403", 
            description = "Forbidden - Only the solution owner, teachers, or admins can submit solutions",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Solution not found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Invalid request or ID format",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Evaluation failed or internal server error",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    public ResponseEntity<?> submitSolution(
            @Parameter(description = "UUID of the solution") @PathVariable String solutionId,
            @Parameter(description = "JWT Bearer token") @RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        try {
            // Extract userId and roles from JWT token
            String currentUserId = jwtUtil.extractUserId(authorizationHeader);
            List<String> roles = jwtUtil.extractRoles(authorizationHeader);
            
            // Get the solution to verify ownership
            var solutionQuery = new GetSolutionByIdQuery(new SolutionId(UUID.fromString(solutionId)));
            var solutionOptional = solutionQueryService.handle(solutionQuery);
            
            if (solutionOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ErrorResponse("Solution not found: " + solutionId));
            }
            
            var solution = solutionOptional.get();
            String solutionOwnerId = solution.getStudentId().id().toString();
            
            // Authorization check: Only solution owner, teachers, or admins can submit
            boolean isOwner = currentUserId.equals(solutionOwnerId);
            boolean isTeacherOrAdmin = roles.contains("TEACHER") || roles.contains("ADMIN");
            
            if (!isOwner && !isTeacherOrAdmin) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ErrorResponse("Access denied. Only the solution owner, teachers, or admins can submit this solution for evaluation."));
            }
            
            // Get the code from the solution
            String code = solution.getCode();
            
            // Transform to domain command using solution's code
            var command = SubmitSolutionCommandFromResourceAssembler.toCommandFromResource(
                solutionId, 
                code, 
                currentUserId
            );

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