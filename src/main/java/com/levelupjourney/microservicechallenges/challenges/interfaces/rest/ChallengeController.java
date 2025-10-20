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
import com.levelupjourney.microservicechallenges.shared.infrastructure.security.JwtUtil;
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
    private final JwtUtil jwtUtil;

    public ChallengeController(ChallengeCommandService challengeCommandService,
                               ChallengeQueryService challengeQueryService,
                               JwtUtil jwtUtil) {
        this.challengeCommandService = challengeCommandService;
        this.challengeQueryService = challengeQueryService;
        this.jwtUtil = jwtUtil;
    }

    // Create a new challenge
    @PostMapping
    public ResponseEntity<ChallengeResource> createChallenge(
            @RequestBody CreateChallengeResource resource,
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        // Extract teacherId from JWT token
        String teacherId = jwtUtil.extractUserId(authorizationHeader);
        
        // Transform resource to domain command with teacherId from token
        var command = CreateChallengeCommandFromResourceAssembler.toCommandFromResource(resource, teacherId);

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
    @GetMapping("/{challengeId}")
    public ResponseEntity<ChallengeResource> getChallengeById(@PathVariable String challengeId) {
        // Transform path variable to domain query
        var query = new GetChallengeByIdQuery(new ChallengeId(UUID.fromString(challengeId)));

        // Execute query through domain service
        var challenge = challengeQueryService.handle(query);

        // Transform domain entity to response resource if found
        if (challenge.isPresent()) {
            var challengeResource = ChallengeResourceFromEntityAssembler.toResourceFromEntity(challenge.get());
            return new ResponseEntity<>(challengeResource, HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // Get all published challenges
    @GetMapping
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
    @GetMapping("/teachers/{teacherId}")
    public ResponseEntity<List<ChallengeResource>> getChallengesByTeacherId(@PathVariable String teacherId) {
        // Transform path variable to domain query
        var query = new GetChallengesByTeacherIdQuery(new TeacherId(UUID.fromString(teacherId)));

        // Execute query through domain service
        var challenges = challengeQueryService.handle(query);

        // Transform domain entities to response resources
        var challengeResources = challenges.stream()
                .map(ChallengeResourceFromEntityAssembler::toResourceFromEntity)
                .collect(Collectors.toList());

        return new ResponseEntity<>(challengeResources, HttpStatus.OK);
    }

    // Update an existing challenge (including status changes like publishing)
    @PatchMapping("/{challengeId}")
    public ResponseEntity<ChallengeResource> updateChallenge(@PathVariable String challengeId,
                                                             @RequestBody UpdateChallengeResource resource) {
        try {
            // Transform resource to domain command
            var command = UpdateChallengeCommandFromResourceAssembler.toCommandFromResource(challengeId, resource);

            // Execute command through domain service
            challengeCommandService.handle(command);

            // Retrieve updated challenge for response
            var query = new GetChallengeByIdQuery(new ChallengeId(UUID.fromString(challengeId)));
            var challenge = challengeQueryService.handle(query);

            // Transform domain entity to response resource
            if (challenge.isPresent()) {
                var challengeResource = ChallengeResourceFromEntityAssembler.toResourceFromEntity(challenge.get());
                return new ResponseEntity<>(challengeResource, HttpStatus.OK);
            }

            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IllegalStateException e) {
            // Handle validation errors (e.g., publishing without required code versions/tests)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (IllegalArgumentException e) {
            // Handle invalid challenge ID or other argument errors
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Unassign a tag from a challenge
    @DeleteMapping("/{challengeId}/tags/{tagId}")
    @Operation(summary = "Unassign tag from challenge", description = "Unassign a specific tag from a challenge")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Tag successfully unassigned from challenge"),
        @ApiResponse(responseCode = "404", description = "Challenge or tag not found")
    })
    public ResponseEntity<Void> unassignTagFromChallenge(@PathVariable String challengeId,
                                                        @PathVariable String tagId) {
        try {
            // Transform path variables to domain command
            var command = UnassignTagFromChallengeCommandFromResourceAssembler.toCommandFromResource(challengeId, tagId);

            // Execute command through domain service
            challengeCommandService.handle(command);

            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Get tags for a specific challenge
    @GetMapping("/{challengeId}/tags")
    @Operation(summary = "Get challenge tags", description = "Retrieve all tags associated with a specific challenge")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved challenge tags"),
        @ApiResponse(responseCode = "404", description = "Challenge not found"),
        @ApiResponse(responseCode = "400", description = "Invalid challenge ID format")
    })
    public ResponseEntity<List<TagResource>> getChallengeTags(@PathVariable String challengeId) {
        try {
            // Get challenge by ID
            var query = new GetChallengeByIdQuery(new ChallengeId(UUID.fromString(challengeId)));
            var challenge = challengeQueryService.handle(query);

            if (challenge.isPresent()) {
                // Transform challenge tags to response resources
                var tagResources = challenge.get().getTags().stream()
                        .map(TagResourceFromEntityAssembler::toResourceFromEntity)
                        .collect(Collectors.toList());

                return new ResponseEntity<>(tagResources, HttpStatus.OK);
            }

            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
