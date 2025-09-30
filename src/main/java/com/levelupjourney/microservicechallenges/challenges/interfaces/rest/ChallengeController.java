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
    public ResponseEntity<ChallengeResource> createChallenge(@RequestBody CreateChallengeResource resource) {
        // Transform resource to domain command
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

    // Get challenges by teacher ID
    @GetMapping("/teacher/{teacherId}")
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

    // Update an existing challenge
    @PutMapping("/{challengeId}")
    public ResponseEntity<ChallengeResource> updateChallenge(@PathVariable String challengeId,
                                                             @RequestBody UpdateChallengeResource resource) {
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
    }

    // Publish a challenge
    @PatchMapping("/{challengeId}/publish")
    public ResponseEntity<ChallengeResource> publishChallenge(@PathVariable String challengeId) {
        // Transform path variable to domain command
        var command = PublishChallengeCommandFromResourceAssembler.toCommandFromResource(
                new PublishChallengeResource(challengeId));

        // Execute command through domain service
        var updatedChallengeId = challengeCommandService.handle(command);

        // Retrieve published challenge for response
        var query = new GetChallengeByIdQuery(updatedChallengeId);
        var challenge = challengeQueryService.handle(query);

        // Transform domain entity to response resource
        if (challenge.isPresent()) {
            var challengeResource = ChallengeResourceFromEntityAssembler.toResourceFromEntity(challenge.get());
            return new ResponseEntity<>(challengeResource, HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // Start a challenge (for students)
    @PostMapping("/{challengeId}/start")
    public ResponseEntity<StartChallengeResponseResource> startChallenge(@PathVariable String challengeId,
                                                                        @RequestBody StartChallengeResource resource) {
        // Transform resource to domain command
        var command = StartChallengeCommandFromResourceAssembler.toCommandFromResource(resource);

        // Execute command through domain service
        challengeCommandService.handle(command);

        // Transform command data to response resource
        var responseResource = StartChallengeResponseResourceFromCommandAssembler.toResourceFromCommand(command);

        return new ResponseEntity<>(responseResource, HttpStatus.OK);
    }
}
