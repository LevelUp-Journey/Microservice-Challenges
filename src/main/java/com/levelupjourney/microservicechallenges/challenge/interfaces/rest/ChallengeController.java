package com.levelupjourney.microservicechallenges.challenge.interfaces.rest;

import com.levelupjourney.microservicechallenges.challenge.domain.model.queries.GetChallengeByIdQuery;
import com.levelupjourney.microservicechallenges.challenge.domain.services.ChallengeCommandService;
import com.levelupjourney.microservicechallenges.challenge.domain.services.ChallengeQueryService;
import com.levelupjourney.microservicechallenges.challenge.interfaces.rest.resources.*;
import com.levelupjourney.microservicechallenges.challenge.interfaces.rest.transform.ChallengeCommandFromResourceAssembler;
import com.levelupjourney.microservicechallenges.challenge.interfaces.rest.transform.ChallengeQueryFromParametersAssembler;
import com.levelupjourney.microservicechallenges.challenge.interfaces.rest.transform.ChallengeResourceFromEntityAssembler;
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
 * REST Controller for Challenge aggregate
 * Follows RESTful conventions and DDD best practices
 */
@RestController
@RequestMapping("/api/v1/challenges")
@CrossOrigin(origins = "*")
@Tag(name = "Challenges", description = "Challenge management operations")
public class ChallengeController {

    private final ChallengeCommandService challengeCommandService;
    private final ChallengeQueryService challengeQueryService;

    public ChallengeController(ChallengeCommandService challengeCommandService,
                             ChallengeQueryService challengeQueryService) {
        this.challengeCommandService = challengeCommandService;
        this.challengeQueryService = challengeQueryService;
    }

    // POST /api/v1/challenges - Create new challenge
    @PostMapping
    @Operation(summary = "Create a new challenge", description = "Creates a new challenge with title, description, and difficulty level")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Challenge created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    public ResponseEntity<ChallengeResource> createChallenge(@RequestBody CreateChallengeResource resource) {
        try {
            var command = ChallengeCommandFromResourceAssembler.toCommandFromResource(resource);
            var result = challengeCommandService.handle(command);
            
            if (result.isPresent()) {
                var challengeResource = ChallengeResourceFromEntityAssembler.toResourceFromEntity(result.get());
                return new ResponseEntity<>(challengeResource, HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // GET /api/v1/challenges - Get all challenges
    @GetMapping
    @Operation(summary = "Get all challenges", description = "Retrieves all challenges with optional filters by state or teacher")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Challenges retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<ChallengeResource>> getAllChallenges(
            @Parameter(description = "Filter by challenge state (DRAFT, PUBLISHED)") @RequestParam(required = false) String state,
            @Parameter(description = "Filter by teacher ID") @RequestParam(required = false) String teacherId) {
        try {
            List<ChallengeResource> challenges;
            
            if (state != null) {
                var query = ChallengeQueryFromParametersAssembler.toGetChallengesByStateQuery(state);
                challenges = challengeQueryService.handle(query).stream()
                        .map(ChallengeResourceFromEntityAssembler::toResourceFromEntity)
                        .collect(Collectors.toList());
            } else if (teacherId != null) {
                var query = ChallengeQueryFromParametersAssembler.toGetChallengesByTeacherIdQuery(teacherId);
                challenges = challengeQueryService.handle(query).stream()
                        .map(ChallengeResourceFromEntityAssembler::toResourceFromEntity)
                        .collect(Collectors.toList());
            } else {
                var query = ChallengeQueryFromParametersAssembler.toGetAllChallengesQuery();
                challenges = challengeQueryService.handle(query).stream()
                        .map(ChallengeResourceFromEntityAssembler::toResourceFromEntity)
                        .collect(Collectors.toList());
            }
            
            return new ResponseEntity<>(challenges, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // GET /api/v1/challenges/{challengeId} - Get challenge by ID
    @GetMapping("/{challengeId}")
    @Operation(summary = "Get challenge by ID", description = "Retrieves a specific challenge by its identifier")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Challenge found and retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Challenge not found"),
        @ApiResponse(responseCode = "400", description = "Invalid challenge ID format")
    })
    public ResponseEntity<ChallengeResource> getChallengeById(
            @Parameter(description = "Challenge unique identifier") @PathVariable String challengeId) {
        try {
            GetChallengeByIdQuery query = ChallengeQueryFromParametersAssembler.toGetChallengeByIdQuery(challengeId);
            var result = challengeQueryService.handle(query);
            
            if (result.isPresent()) {
                var challengeResource = ChallengeResourceFromEntityAssembler.toResourceFromEntity(result.get());
                return new ResponseEntity<>(challengeResource, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // PUT /api/v1/challenges/{challengeId} - Update challenge
    @PutMapping("/{challengeId}")
    @Operation(summary = "Update challenge", description = "Updates an existing challenge (only by the challenge owner)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Challenge updated successfully"),
        @ApiResponse(responseCode = "404", description = "Challenge not found"),
        @ApiResponse(responseCode = "403", description = "Not authorized to update this challenge"),
        @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    public ResponseEntity<ChallengeResource> updateChallenge(
            @Parameter(description = "Challenge unique identifier") @PathVariable String challengeId,
            @Parameter(description = "Teacher ID for authorization") @RequestParam String teacherId,
            @RequestBody UpdateChallengeResource resource) {
        try {
            var command = ChallengeCommandFromResourceAssembler.toCommandFromResource(challengeId, teacherId, resource);
            var result = challengeCommandService.handle(command);
            
            if (result.isPresent()) {
                var challengeResource = ChallengeResourceFromEntityAssembler.toResourceFromEntity(result.get());
                return new ResponseEntity<>(challengeResource, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // POST /api/v1/challenges/{challengeId}/publish - Publish challenge
    @PostMapping("/{challengeId}/publish")
    @Operation(summary = "Publish challenge", description = "Makes a challenge available to students")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Challenge published successfully"),
        @ApiResponse(responseCode = "409", description = "Challenge already published"),
        @ApiResponse(responseCode = "400", description = "Invalid challenge ID or challenge cannot be published")
    })
    public ResponseEntity<Void> publishChallenge(
            @Parameter(description = "Challenge unique identifier") @PathVariable String challengeId) {
        try {
            var command = ChallengeCommandFromResourceAssembler.toCommandFromResource(challengeId);
            challengeCommandService.handle(command);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // POST /api/v1/challenges/{challengeId}/versions - Create challenge version
    @PostMapping("/{challengeId}/versions")
    @Operation(summary = "Create challenge version", description = "Creates a new version of an existing challenge")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Challenge version created successfully"),
        @ApiResponse(responseCode = "409", description = "Version creation conflict"),
        @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    public ResponseEntity<ChallengeVersionResource> createChallengeVersion(
            @Parameter(description = "Parent challenge unique identifier") @PathVariable String challengeId,
            @RequestBody CreateChallengeVersionResource resource) {
        try {
            var command = ChallengeCommandFromResourceAssembler.toCommandFromResource(challengeId, resource);
            var result = challengeCommandService.handle(command);
            
            if (result.isPresent()) {
                var versionResource = ChallengeResourceFromEntityAssembler.toResourceFromEntity(result.get());
                return new ResponseEntity<>(versionResource, HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // GET /api/v1/challenges/{challengeId}/tests - Get tests for challenge
    @GetMapping("/{challengeId}/tests")
    @Operation(summary = "Get challenge tests", description = "Retrieves all test cases for a specific challenge")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Tests retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid challenge ID")
    })
    public ResponseEntity<List<TestResource>> getChallengeTests(
            @Parameter(description = "Challenge unique identifier") @PathVariable String challengeId) {
        try {
            var query = ChallengeQueryFromParametersAssembler.toGetChallengeTestsByChallengeIdQuery(challengeId);
            var tests = challengeQueryService.handle(query).stream()
                    .map(ChallengeResourceFromEntityAssembler::toResourceFromEntity)
                    .collect(Collectors.toList());
            
            return new ResponseEntity<>(tests, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // GET /api/v1/challenges/{challengeId}/stars/count - Get stars count
    @GetMapping("/{challengeId}/stars/count")
    @Operation(summary = "Get challenge stars count", description = "Gets the total number of stars (favorites) for a challenge")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Stars count retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid challenge ID")
    })
    public ResponseEntity<Integer> getChallengeStarsCount(
            @Parameter(description = "Challenge unique identifier") @PathVariable String challengeId) {
        try {
            var query = ChallengeQueryFromParametersAssembler.toGetChallengeStarsAmountQuery(challengeId);
            var count = challengeQueryService.handle(query);
            
            return new ResponseEntity<>(count, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // POST /api/v1/challenges/{challengeId}/stars - Star challenge
    @PostMapping("/{challengeId}/stars")
    @Operation(summary = "Star a challenge", description = "Adds a star (favorite) to a challenge by a student")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Challenge starred successfully"),
        @ApiResponse(responseCode = "409", description = "Challenge already starred by this student"),
        @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    public ResponseEntity<Void> starChallenge(
            @Parameter(description = "Challenge unique identifier") @PathVariable String challengeId,
            @Parameter(description = "Student unique identifier") @RequestParam String studentId) {
        try {
            var command = ChallengeCommandFromResourceAssembler.toStarCommandFromResource(challengeId, studentId);
            challengeCommandService.handle(command);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // DELETE /api/v1/challenges/{challengeId}/stars - Unstar challenge
    @DeleteMapping("/{challengeId}/stars")
    @Operation(summary = "Unstar a challenge", description = "Removes a star (favorite) from a challenge by a student")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Challenge unstarred successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    public ResponseEntity<Void> unstarChallenge(
            @Parameter(description = "Challenge unique identifier") @PathVariable String challengeId,
            @Parameter(description = "Student unique identifier") @RequestParam String studentId) {
        try {
            var command = ChallengeCommandFromResourceAssembler.toUnstarCommandFromResource(challengeId, studentId);
            challengeCommandService.handle(command);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // GET /api/v1/challenges/starred - Get starred challenges by student
    @GetMapping("/starred")
    @Operation(summary = "Get starred challenges", description = "Retrieves all challenges starred by a specific student")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Starred challenges retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid student ID")
    })
    public ResponseEntity<List<ChallengeResource>> getStarredChallenges(
            @Parameter(description = "Student unique identifier") @RequestParam String studentId) {
        try {
            var query = ChallengeQueryFromParametersAssembler.toGetStarredChallengesByStudentIdQuery(studentId);
            var challenges = challengeQueryService.handle(query).stream()
                    .map(ChallengeResourceFromEntityAssembler::toResourceFromEntity)
                    .collect(Collectors.toList());
            
            return new ResponseEntity<>(challenges, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
