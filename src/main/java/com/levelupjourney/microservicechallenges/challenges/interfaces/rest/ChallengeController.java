package com.levelupjourney.microservicechallenges.challenges.interfaces.rest;

import com.levelupjourney.microservicechallenges.challenges.domain.model.queries.GetAllPublishedChallengesQuery;
import com.levelupjourney.microservicechallenges.challenges.domain.model.queries.GetChallengeByIdQuery;
import com.levelupjourney.microservicechallenges.challenges.domain.model.queries.GetChallengesByTeacherIdQuery;
import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.ChallengeId;
import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.TeacherId;
import com.levelupjourney.microservicechallenges.challenges.domain.services.ChallengeCommandService;
import com.levelupjourney.microservicechallenges.challenges.domain.services.ChallengeQueryService;
import com.levelupjourney.microservicechallenges.challenges.interfaces.rest.resource.*;
import com.levelupjourney.microservicechallenges.challenges.interfaces.rest.transform.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/v1/challenges", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Challenges", description = "Endpoints for managing coding challenges")
public class ChallengeController {

    private final ChallengeCommandService challengeCommandService;
    private final ChallengeQueryService challengeQueryService;

    public ChallengeController(ChallengeCommandService challengeCommandService,
                               ChallengeQueryService challengeQueryService) {
        this.challengeCommandService = challengeCommandService;
        this.challengeQueryService = challengeQueryService;
    }

    // Create a new challenge
    @PostMapping
    @Operation(summary = "Create challenge", description = "Create a new coding challenge. Teacher ID extracted from JWT token.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Challenge created successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ChallengeResource> createChallenge(
            @RequestBody CreateChallengeResource resource) {
        // Transform resource to domain command with teacherId from request
        var command = CreateChallengeCommandFromResourceAssembler.toCommandFromResource(resource);

        // Execute command through domain service
        var challengeId = challengeCommandService.handle(command);

        // Retrieve created challenge for response
        var query = new GetChallengeByIdQuery(challengeId);
        var challenge = challengeQueryService.handle(query);

        // Transform domain entity to response resource
        if (challenge.isPresent()) {
            var challengeResource = ChallengeResourceFromEntityAssembler.toResourceFromEntity(challenge.get());
            return new ResponseEntity<>(challengeResource, HttpStatus.CREATED);
        }

        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Get challenge by ID
    // Ownership validation: Only the owner can view DRAFT/HIDDEN challenges, anyone can view PUBLISHED
    @GetMapping("/{challengeId}")
    @Operation(summary = "Get challenge by ID", description = "Retrieve a specific challenge. Access control: PUBLISHED challenges are public, DRAFT/HIDDEN require ownership.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Challenge retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied - not published and not owner"),
        @ApiResponse(responseCode = "404", description = "Challenge not found")
    })
    public ResponseEntity<?> getChallengeById(
            @PathVariable String challengeId) {
        
        // Transform path variable to domain query
        var query = new GetChallengeByIdQuery(new ChallengeId(UUID.fromString(challengeId)));

        // Execute query through domain service
        var challengeOptional = challengeQueryService.handle(query);

        // Check if challenge exists
        if (challengeOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("Challenge not found with id: " + challengeId));
        }

        var challenge = challengeOptional.get();
        
        // Transform domain entity to response resource
        var challengeResource = ChallengeResourceFromEntityAssembler.toResourceFromEntity(challenge);
        return new ResponseEntity<>(challengeResource, HttpStatus.OK);
    }

    // Get all published challenges
    @GetMapping
    @Operation(summary = "Get all published challenges", description = "Retrieve all challenges with PUBLISHED status. Public endpoint.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Published challenges retrieved successfully")
    })
    public ResponseEntity<List<ChallengeResource>> getAllPublishedChallenges() {
        // Execute query for published challenges
        var query = new GetAllPublishedChallengesQuery();
        var challenges = challengeQueryService.handle(query);

        // Transform domain entities to response resources
        var challengeResources = challenges.stream()
                .map(ChallengeResourceFromEntityAssembler::toResourceFromEntity)
                .collect(Collectors.toList());

        return new ResponseEntity<>(challengeResources, HttpStatus.OK);
    }

    // Get challenges by teacher ID (derived collection)
    // Role-based filtering: Students see only PUBLISHED challenges, Teachers/Admins see all
    @GetMapping("/teachers/{teacherId}")
    @Operation(summary = "Get challenges by teacher", description = "Retrieve challenges by teacher ID. Role-based filtering: Students see only PUBLISHED, Teachers/Admins see all.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Challenges retrieved successfully")
    })
    public ResponseEntity<List<ChallengeResource>> getChallengesByTeacherId(
            @PathVariable String teacherId) {
        
        // Return all challenges for the teacher (no role-based filtering without authentication)
        var query = new GetChallengesByTeacherIdQuery(new TeacherId(UUID.fromString(teacherId)));
        var challenges = challengeQueryService.handle(query);

        // Transform domain entities to response resources
        var challengeResources = challenges.stream()
                .map(ChallengeResourceFromEntityAssembler::toResourceFromEntity)
                .collect(Collectors.toList());

        return new ResponseEntity<>(challengeResources, HttpStatus.OK);
    }

    // Update an existing challenge (including status changes like publishing)
    // Only the challenge owner (teacher) can update their own challenges
    @PatchMapping("/{challengeId}")
    @Operation(
        summary = "Update challenge", 
        description = "Update an existing challenge. All fields are optional - only send the fields you want to update. " +
                      "For example, to publish a challenge, send only: {\"status\": \"PUBLISHED\"}. " +
                      "Only the challenge owner can make updates."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Challenge updated successfully"),
        @ApiResponse(responseCode = "403", description = "Forbidden - not the challenge owner"),
        @ApiResponse(responseCode = "404", description = "Challenge not found"),
        @ApiResponse(responseCode = "400", description = "Validation error")
    })
    public ResponseEntity<?> updateChallenge(
            @PathVariable String challengeId,
            @RequestBody UpdateChallengeResource resource) {
        try {
            // Retrieve the challenge to verify it exists
            var getChallengeQuery = new GetChallengeByIdQuery(new ChallengeId(UUID.fromString(challengeId)));
            var challengeOptional = challengeQueryService.handle(getChallengeQuery);
            
            if (challengeOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Challenge not found with id: " + challengeId));
            }
            
            // Transform resource to domain command
            var command = UpdateChallengeCommandFromResourceAssembler.toCommandFromResource(challengeId, resource);

            // Execute command through domain service
            challengeCommandService.handle(command);

            // Retrieve updated challenge for response
            var updatedChallenge = challengeQueryService.handle(getChallengeQuery);

            // Transform domain entity to response resource
            if (updatedChallenge.isPresent()) {
                var challengeResource = ChallengeResourceFromEntityAssembler.toResourceFromEntity(updatedChallenge.get());
                return new ResponseEntity<>(challengeResource, HttpStatus.OK);
            }

            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (IllegalStateException e) {
            // Handle validation errors (e.g., publishing without required code versions/tests)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(e.getMessage()));
        } catch (IllegalArgumentException e) {
            // Handle invalid challenge ID or other argument errors
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("Invalid request: " + e.getMessage()));
        }
    }

    // Delete a challenge by ID
    // Only the challenge owner (teacher) can delete their own challenges
    @DeleteMapping("/{challengeId}")
    @Operation(summary = "Delete challenge", description = "Delete a challenge by ID. Only the challenge owner can delete their own challenges.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Challenge deleted successfully"),
        @ApiResponse(responseCode = "403", description = "Forbidden - not the challenge owner"),
        @ApiResponse(responseCode = "404", description = "Challenge not found")
    })
    public ResponseEntity<?> deleteChallenge(
            @PathVariable String challengeId) {
        try {
            // Retrieve the challenge to verify it exists
            var getChallengeQuery = new GetChallengeByIdQuery(new ChallengeId(UUID.fromString(challengeId)));
            var challengeOptional = challengeQueryService.handle(getChallengeQuery);
            
            if (challengeOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Challenge not found with id: " + challengeId));
            }
            
            // Transform to domain command
            var command = new com.levelupjourney.microservicechallenges.challenges.domain.model.commands.DeleteChallengeCommand(
                new ChallengeId(UUID.fromString(challengeId))
            );

            // Execute delete command through domain service
            challengeCommandService.handle(command);

            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            // Handle invalid challenge ID or other argument errors
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("Invalid request: " + e.getMessage()));
        }
    }
    
    // Simple error response record for consistent error handling
    private record ErrorResponse(String message) {}
}
