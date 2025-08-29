package com.levelupjourney.microservicechallenges.challenge.interfaces.rest;

import com.levelupjourney.microservicechallenges.challenge.domain.model.queries.GetChallengeByIdQuery;
import com.levelupjourney.microservicechallenges.challenge.domain.services.ChallengeCommandService;
import com.levelupjourney.microservicechallenges.challenge.domain.services.ChallengeQueryService;
import com.levelupjourney.microservicechallenges.challenge.interfaces.rest.resources.*;
import com.levelupjourney.microservicechallenges.challenge.interfaces.rest.transform.ChallengeCommandFromResourceAssembler;
import com.levelupjourney.microservicechallenges.challenge.interfaces.rest.transform.ChallengeQueryFromParametersAssembler;
import com.levelupjourney.microservicechallenges.challenge.interfaces.rest.transform.ChallengeResourceFromEntityAssembler;
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
    public ResponseEntity<List<ChallengeResource>> getAllChallenges(
            @RequestParam(required = false) String state,
            @RequestParam(required = false) String teacherId) {
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
    public ResponseEntity<ChallengeResource> getChallengeById(@PathVariable String challengeId) {
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
    public ResponseEntity<ChallengeResource> updateChallenge(
            @PathVariable String challengeId,
            @RequestParam String teacherId,
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
    public ResponseEntity<Void> publishChallenge(@PathVariable String challengeId) {
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
    public ResponseEntity<ChallengeVersionResource> createChallengeVersion(
            @PathVariable String challengeId,
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
    public ResponseEntity<List<TestResource>> getChallengeTests(@PathVariable String challengeId) {
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
    public ResponseEntity<Integer> getChallengeStarsCount(@PathVariable String challengeId) {
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
    public ResponseEntity<Void> starChallenge(
            @PathVariable String challengeId,
            @RequestParam String studentId) {
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
    public ResponseEntity<Void> unstarChallenge(
            @PathVariable String challengeId,
            @RequestParam String studentId) {
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
    public ResponseEntity<List<ChallengeResource>> getStarredChallenges(@RequestParam String studentId) {
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
