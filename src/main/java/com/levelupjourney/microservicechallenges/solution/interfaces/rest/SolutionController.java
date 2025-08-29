package com.levelupjourney.microservicechallenges.solution.interfaces.rest;

import com.levelupjourney.microservicechallenges.solution.domain.services.SolutionCommandService;
import com.levelupjourney.microservicechallenges.solution.domain.services.SolutionQueryService;
import com.levelupjourney.microservicechallenges.solution.interfaces.rest.resources.CreateSolutionResource;
import com.levelupjourney.microservicechallenges.solution.interfaces.rest.resources.SolutionResource;
import com.levelupjourney.microservicechallenges.solution.interfaces.rest.resources.SubmitSolutionResource;
import com.levelupjourney.microservicechallenges.solution.interfaces.rest.resources.UpdateSolutionResource;
import com.levelupjourney.microservicechallenges.solution.interfaces.rest.transform.SolutionCommandFromResourceAssembler;
import com.levelupjourney.microservicechallenges.solution.interfaces.rest.transform.SolutionQueryFromParametersAssembler;
import com.levelupjourney.microservicechallenges.solution.interfaces.rest.transform.SolutionResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST Controller for Solution aggregate
 * Follows RESTful conventions and DDD best practices
 */
@RestController
@RequestMapping("/api/v1/solutions")
@CrossOrigin(origins = "*")
@Tag(name = "Solutions", description = "Student solution management operations")
public class SolutionController {

    private final SolutionCommandService solutionCommandService;
    private final SolutionQueryService solutionQueryService;

    public SolutionController(SolutionCommandService solutionCommandService,
                             SolutionQueryService solutionQueryService) {
        this.solutionCommandService = solutionCommandService;
        this.solutionQueryService = solutionQueryService;
    }

    // POST /api/v1/solutions - Create new solution
    @PostMapping
    @Operation(summary = "Create solution", description = "Creates a new solution draft for a challenge")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Solution created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    public ResponseEntity<SolutionResource> createSolution(@RequestBody CreateSolutionResource resource) {
        try {
            var command = SolutionCommandFromResourceAssembler.toCommandFromResource(resource);
            var result = solutionCommandService.handle(command);
            
            if (result.isPresent()) {
                var solutionResource = SolutionResourceFromEntityAssembler.toResourceFromEntity(result.get());
                return new ResponseEntity<>(solutionResource, HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // GET /api/v1/solutions - Get all solutions
    @GetMapping
    @Operation(summary = "Get all solutions", description = "Retrieves solutions with optional filters by student or challenge")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Solutions retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<SolutionResource>> getAllSolutions(
            @Parameter(description = "Filter by student ID") @RequestParam(required = false) String studentId,
            @Parameter(description = "Filter by challenge ID") @RequestParam(required = false) String challengeId) {
        try {
            List<SolutionResource> solutions;
            
            if (studentId != null) {
                var query = SolutionQueryFromParametersAssembler.toGetSolutionsByStudentIdQuery(studentId);
                solutions = solutionQueryService.handle(query).stream()
                        .map(SolutionResourceFromEntityAssembler::toResourceFromEntity)
                        .collect(Collectors.toList());
            } else if (challengeId != null) {
                var query = SolutionQueryFromParametersAssembler.toGetSolutionsByChallengeIdQuery(challengeId);
                solutions = solutionQueryService.handle(query).stream()
                        .map(SolutionResourceFromEntityAssembler::toResourceFromEntity)
                        .collect(Collectors.toList());
            } else {
                var query = SolutionQueryFromParametersAssembler.toGetAllSolutionsQuery();
                solutions = solutionQueryService.handle(query).stream()
                        .map(SolutionResourceFromEntityAssembler::toResourceFromEntity)
                        .collect(Collectors.toList());
            }
            
            return new ResponseEntity<>(solutions, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // GET /api/v1/solutions/{solutionId} - Get solution by ID
    @GetMapping("/{solutionId}")
    @Operation(summary = "Get solution by ID", description = "Retrieves a specific solution by its identifier")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Solution found and retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Solution not found"),
        @ApiResponse(responseCode = "400", description = "Invalid solution ID format")
    })
    public ResponseEntity<SolutionResource> getSolutionById(
            @Parameter(description = "Solution unique identifier") @PathVariable String solutionId) {
        try {
            var query = SolutionQueryFromParametersAssembler.toGetSolutionByIdQuery(solutionId);
            var result = solutionQueryService.handle(query);
            
            if (result.isPresent()) {
                var solutionResource = SolutionResourceFromEntityAssembler.toResourceFromEntity(result.get());
                return new ResponseEntity<>(solutionResource, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // PUT /api/v1/solutions/{solutionId} - Update solution
    @PutMapping("/{solutionId}")
    @Operation(summary = "Update solution", description = "Updates an existing solution draft (only for non-submitted solutions)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Solution updated successfully"),
        @ApiResponse(responseCode = "404", description = "Solution not found"),
        @ApiResponse(responseCode = "400", description = "Invalid request data or solution already submitted")
    })
    public ResponseEntity<SolutionResource> updateSolution(
            @Parameter(description = "Solution unique identifier") @PathVariable String solutionId,
            @RequestBody UpdateSolutionResource resource) {
        try {
            var command = SolutionCommandFromResourceAssembler.toCommandFromResource(solutionId, resource);
            var result = solutionCommandService.handle(command);
            
            if (result.isPresent()) {
                var solutionResource = SolutionResourceFromEntityAssembler.toResourceFromEntity(result.get());
                return new ResponseEntity<>(solutionResource, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // POST /api/v1/solutions/submit - Submit solution (requires challengeId and studentId as params)
    @PostMapping("/submit")
    @Operation(summary = "Submit solution for evaluation", description = "Submits a student's solution for automatic testing and evaluation")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Solution submitted successfully"),
        @ApiResponse(responseCode = "404", description = "Challenge or student not found"),
        @ApiResponse(responseCode = "409", description = "Solution already submitted"),
        @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    public ResponseEntity<SolutionResource> submitSolution(
            @Parameter(description = "Challenge unique identifier") @RequestParam String challengeId,
            @Parameter(description = "Student unique identifier") @RequestParam String studentId,
            @RequestBody SubmitSolutionResource resource) {
        try {
            var command = SolutionCommandFromResourceAssembler.toCommandFromResource(challengeId, resource);
            var result = solutionCommandService.handle(command);
            
            if (result.isPresent()) {
                var solutionResource = SolutionResourceFromEntityAssembler.toResourceFromEntity(result.get());
                return new ResponseEntity<>(solutionResource, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // GET /api/v1/solutions/student/{studentId}/challenge/{challengeId} - Get student solution for challenge
    @GetMapping("/student/{studentId}/challenge/{challengeId}")
    @Operation(summary = "Get student's solution for challenge", description = "Retrieves a specific student's solution for a particular challenge")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Solution found and retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Solution not found for this student-challenge combination"),
        @ApiResponse(responseCode = "400", description = "Invalid student or challenge ID format")
    })
    public ResponseEntity<SolutionResource> getSolutionByStudentAndChallenge(
            @Parameter(description = "Student unique identifier") @PathVariable String studentId,
            @Parameter(description = "Challenge unique identifier") @PathVariable String challengeId) {
        try {
            var query = SolutionQueryFromParametersAssembler.toGetSolutionByStudentIdAndChallengeIdQuery(studentId, challengeId);
            var result = solutionQueryService.handle(query);
            
            if (result.isPresent()) {
                var solutionResource = SolutionResourceFromEntityAssembler.toResourceFromEntity(result.get());
                return new ResponseEntity<>(solutionResource, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
