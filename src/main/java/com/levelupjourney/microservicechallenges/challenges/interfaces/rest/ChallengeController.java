package com.levelupjourney.microservicechallenges.challenges.interfaces.rest;

import com.levelupjourney.microservicechallenges.challenges.domain.model.commands.AddGuideCommand;
import com.levelupjourney.microservicechallenges.challenges.domain.model.commands.LikeChallengeCommand;
import com.levelupjourney.microservicechallenges.challenges.domain.model.commands.UnlikeChallengeCommand;
import com.levelupjourney.microservicechallenges.challenges.domain.model.queries.GetAllPublishedChallengesQuery;
import com.levelupjourney.microservicechallenges.challenges.domain.model.queries.GetChallengeByIdQuery;
import com.levelupjourney.microservicechallenges.challenges.domain.model.queries.GetChallengesByTeacherIdQuery;
import com.levelupjourney.microservicechallenges.challenges.domain.model.queries.GetPublishedChallengesByTeacherIdQuery;
import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.ChallengeId;
import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.TeacherId;
import com.levelupjourney.microservicechallenges.challenges.domain.services.ChallengeCommandService;
import com.levelupjourney.microservicechallenges.challenges.domain.services.ChallengeQueryService;
import com.levelupjourney.microservicechallenges.challenges.domain.services.CodeVersionQueryService;
import com.levelupjourney.microservicechallenges.challenges.infrastructure.persistence.jpa.repositories.ChallengeLikeRepository;
import com.levelupjourney.microservicechallenges.challenges.interfaces.rest.resource.*;
import com.levelupjourney.microservicechallenges.challenges.interfaces.rest.transform.*;
import com.levelupjourney.microservicechallenges.shared.infrastructure.security.JwtUtil;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/v1/challenges", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Challenges", description = "Endpoints for managing coding challenges")
@SecurityRequirement(name = "bearerAuth")
public class ChallengeController {

    private final ChallengeCommandService challengeCommandService;
    private final ChallengeQueryService challengeQueryService;
    private final CodeVersionQueryService codeVersionQueryService;
    private final ChallengeLikeRepository challengeLikeRepository;
    private final JwtUtil jwtUtil;

    public ChallengeController(ChallengeCommandService challengeCommandService,
                               ChallengeQueryService challengeQueryService,
                               CodeVersionQueryService codeVersionQueryService,
                               ChallengeLikeRepository challengeLikeRepository,
                               JwtUtil jwtUtil) {
        this.challengeCommandService = challengeCommandService;
        this.challengeQueryService = challengeQueryService;
        this.codeVersionQueryService = codeVersionQueryService;
        this.challengeLikeRepository = challengeLikeRepository;
        this.jwtUtil = jwtUtil;
    }

    // Create a new challenge
    @PostMapping
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    @Operation(summary = "Create challenge", description = "Create a new coding challenge. Only accessible by TEACHER and ADMIN roles. Teacher ID extracted from JWT token.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Challenge created successfully"),
        @ApiResponse(responseCode = "400", description = "Bad request - validation error (e.g., experience points exceed difficulty max score)"),
        @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> createChallenge(
            @RequestBody CreateChallengeResource resource,
            HttpServletRequest request) {
        try {
            // Extract Authorization header from request
            String authorizationHeader = request.getHeader("Authorization");
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
                // Fetch like data (newly created challenge has 0 likes and is not liked by creator)
                UUID challengeUuid = challengeId.id();
                UUID userUuid = UUID.fromString(teacherId);
                
                boolean userLiked = challengeLikeRepository.existsByChallengeIdAndUserId(challengeUuid, userUuid);
                long likesCount = challengeLikeRepository.countByChallengeId(challengeUuid);
                
                var challengeResource = ChallengeResourceFromEntityAssembler.toResourceFromEntity(challenge.get(), userLiked, likesCount);
                return new ResponseEntity<>(challengeResource, HttpStatus.CREATED);
            }

            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (IllegalArgumentException e) {
            // Handle validation errors (e.g., experience points exceeding difficulty max score)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("Validation error: " + e.getMessage()));
        }
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
            @PathVariable String challengeId,
            HttpServletRequest request) {
        
        // Extract Authorization header from request
        String authorizationHeader = request.getHeader("Authorization");
        
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
        
        // Validate access based on challenge status
        if (challenge.getStatus() != com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.ChallengeStatus.PUBLISHED) {
            // Challenge is DRAFT or HIDDEN - only owner can access
            String userIdFromToken = jwtUtil.extractUserId(authorizationHeader);
            String challengeOwnerId = challenge.getTeacherId().id().toString();
            
            if (userIdFromToken == null || !userIdFromToken.equals(challengeOwnerId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResponse("Access denied. This challenge is not published and you are not the owner."));
            }
        }
        
        // Fetch like data
        String userId = jwtUtil.extractUserId(authorizationHeader);
        UUID challengeUuid = UUID.fromString(challengeId);
        UUID userUuid = UUID.fromString(userId);
        
        boolean userLiked = challengeLikeRepository.existsByChallengeIdAndUserId(challengeUuid, userUuid);
        long likesCount = challengeLikeRepository.countByChallengeId(challengeUuid);
        
        // Transform domain entity to response resource with like data
        var challengeResource = ChallengeResourceFromEntityAssembler.toResourceFromEntity(challenge, userLiked, likesCount);
        return new ResponseEntity<>(challengeResource, HttpStatus.OK);
    }

    // Get all published challenges
    @GetMapping
    @Operation(summary = "Get all published challenges", description = "Retrieve all challenges with PUBLISHED status. Public endpoint.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Published challenges retrieved successfully")
    })
    public ResponseEntity<List<ChallengeResource>> getAllPublishedChallenges(HttpServletRequest request) {
        // Execute query for published challenges
        var query = new GetAllPublishedChallengesQuery();
        var challenges = challengeQueryService.handle(query);

        // Extract user ID from JWT
        String authorizationHeader = request.getHeader("Authorization");
        String userId = jwtUtil.extractUserId(authorizationHeader);
        UUID userUuid = UUID.fromString(userId);

        // Batch fetch like data
        List<UUID> challengeIds = challenges.stream()
                .map(challenge -> challenge.getId().id())
                .collect(Collectors.toList());

        // Get likes count for all challenges
        var likesCountResults = challengeLikeRepository.countByChallengeIdIn(challengeIds);
        var likesCountMap = likesCountResults.stream()
                .collect(Collectors.toMap(
                        result -> (UUID) result[0],
                        result -> (Long) result[1]
                ));

        // Get challenges liked by user
        var likedChallengeIds = challengeLikeRepository.findLikedChallengeIdsByUserIdAndChallengeIdIn(challengeIds, userUuid);
        var likedSet = likedChallengeIds.stream().collect(Collectors.toSet());

        // Transform domain entities to response resources with like data
        var challengeResources = challenges.stream()
                .map(challenge -> {
                    UUID challengeId = challenge.getId().id();
                    boolean userLiked = likedSet.contains(challengeId);
                    long likesCount = likesCountMap.getOrDefault(challengeId, 0L);
                    return ChallengeResourceFromEntityAssembler.toResourceFromEntity(challenge, userLiked, likesCount);
                })
                .collect(Collectors.toList());

        return new ResponseEntity<>(challengeResources, HttpStatus.OK);
    }

    // Search published challenges with filters
    @GetMapping("/search")
    @Operation(
        summary = "Search published challenges", 
        description = "Search challenges with PUBLISHED status using optional filters. " +
                      "All filters are optional and can be combined. " +
                      "Examples: /challenges/search?name=hello, /challenges/search?name=h&difficulty=EASY"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Challenges found successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid filter parameters")
    })
    public ResponseEntity<List<ChallengeResource>> searchPublishedChallenges(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String difficulty,
            @RequestParam(required = false) String tags,
            HttpServletRequest request) {
        
        // Create search query with filters
        var query = new com.levelupjourney.microservicechallenges.challenges.domain.model.queries.SearchPublishedChallengesQuery(
            name,
            difficulty,
            tags
        );

        // Execute search query
        var challenges = challengeQueryService.handle(query);

        // Extract user ID from JWT
        String authorizationHeader = request.getHeader("Authorization");
        String userId = jwtUtil.extractUserId(authorizationHeader);
        UUID userUuid = UUID.fromString(userId);

        // Batch fetch like data
        List<UUID> challengeIds = challenges.stream()
                .map(challenge -> challenge.getId().id())
                .collect(Collectors.toList());

        // Get likes count for all challenges
        var likesCountResults = challengeLikeRepository.countByChallengeIdIn(challengeIds);
        var likesCountMap = likesCountResults.stream()
                .collect(Collectors.toMap(
                        result -> (UUID) result[0],
                        result -> (Long) result[1]
                ));

        // Get challenges liked by user
        var likedChallengeIds = challengeLikeRepository.findLikedChallengeIdsByUserIdAndChallengeIdIn(challengeIds, userUuid);
        var likedSet = likedChallengeIds.stream().collect(Collectors.toSet());

        // Transform domain entities to response resources with like data
        var challengeResources = challenges.stream()
                .map(challenge -> {
                    UUID challengeId = challenge.getId().id();
                    boolean userLiked = likedSet.contains(challengeId);
                    long likesCount = likesCountMap.getOrDefault(challengeId, 0L);
                    return ChallengeResourceFromEntityAssembler.toResourceFromEntity(challenge, userLiked, likesCount);
                })
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
            @PathVariable String teacherId,
            HttpServletRequest request) {
        
        // Extract Authorization header from request
        String authorizationHeader = request.getHeader("Authorization");
        
        // Extract user roles from JWT token
        List<String> roles = jwtUtil.extractRoles(authorizationHeader);
        
        // Determine which query to use based on user role
        List<com.levelupjourney.microservicechallenges.challenges.domain.model.aggregates.Challenge> challenges;
        
        if (roles.contains("ROLE_STUDENT")) {
            // Students can only see PUBLISHED challenges
            var query = new GetPublishedChallengesByTeacherIdQuery(new TeacherId(UUID.fromString(teacherId)));
            challenges = challengeQueryService.handle(query);
        } else {
            // Teachers and Admins can see all challenges (DRAFT and PUBLISHED)
            var query = new GetChallengesByTeacherIdQuery(new TeacherId(UUID.fromString(teacherId)));
            challenges = challengeQueryService.handle(query);
        }

        // Extract user ID from JWT
        String userId = jwtUtil.extractUserId(authorizationHeader);
        UUID userUuid = UUID.fromString(userId);

        // Batch fetch like data
        List<UUID> challengeIds = challenges.stream()
                .map(challenge -> challenge.getId().id())
                .collect(Collectors.toList());

        // Get likes count for all challenges
        var likesCountResults = challengeLikeRepository.countByChallengeIdIn(challengeIds);
        var likesCountMap = likesCountResults.stream()
                .collect(Collectors.toMap(
                        result -> (UUID) result[0],
                        result -> (Long) result[1]
                ));

        // Get challenges liked by user
        var likedChallengeIds = challengeLikeRepository.findLikedChallengeIdsByUserIdAndChallengeIdIn(challengeIds, userUuid);
        var likedSet = likedChallengeIds.stream().collect(Collectors.toSet());

        // Transform domain entities to response resources with like data
        var challengeResources = challenges.stream()
                .map(challenge -> {
                    UUID challengeId = challenge.getId().id();
                    boolean userLiked = likedSet.contains(challengeId);
                    long likesCount = likesCountMap.getOrDefault(challengeId, 0L);
                    return ChallengeResourceFromEntityAssembler.toResourceFromEntity(challenge, userLiked, likesCount);
                })
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
        @ApiResponse(responseCode = "400", description = "Bad request - validation error (e.g., experience points exceed difficulty max score)"),
        @ApiResponse(responseCode = "403", description = "Forbidden - not the challenge owner"),
        @ApiResponse(responseCode = "404", description = "Challenge not found")
    })
    public ResponseEntity<?> updateChallenge(
            @PathVariable String challengeId,
            @RequestBody UpdateChallengeResource resource,
            HttpServletRequest request) {
        try {
            // Extract Authorization header from request
            String authorizationHeader = request.getHeader("Authorization");
            
            // Extract userId from JWT token
            String userIdFromToken = jwtUtil.extractUserId(authorizationHeader);
            
            // Retrieve the challenge to verify ownership
            var getChallengeQuery = new GetChallengeByIdQuery(new ChallengeId(UUID.fromString(challengeId)));
            var challengeOptional = challengeQueryService.handle(getChallengeQuery);
            
            if (challengeOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Challenge not found with id: " + challengeId));
            }
            
            var challenge = challengeOptional.get();
            
            // Verify ownership: Only the teacher who created the challenge can update it
            String challengeOwnerId = challenge.getTeacherId().id().toString();
            
            if (userIdFromToken == null || !userIdFromToken.equals(challengeOwnerId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResponse("You are not authorized to update this challenge. Only the challenge owner can make updates."));
            }
            
            // Transform resource to domain command
            var command = UpdateChallengeCommandFromResourceAssembler.toCommandFromResource(challengeId, resource);

            // Execute command through domain service
            challengeCommandService.handle(command);

            // Retrieve updated challenge for response
            var updatedChallenge = challengeQueryService.handle(getChallengeQuery);

            // Transform domain entity to response resource
            if (updatedChallenge.isPresent()) {
                // Fetch like data
                UUID challengeUuid = UUID.fromString(challengeId);
                UUID userUuid = UUID.fromString(userIdFromToken);
                
                boolean userLiked = challengeLikeRepository.existsByChallengeIdAndUserId(challengeUuid, userUuid);
                long likesCount = challengeLikeRepository.countByChallengeId(challengeUuid);
                
                var challengeResource = ChallengeResourceFromEntityAssembler.toResourceFromEntity(updatedChallenge.get(), userLiked, likesCount);
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
            @PathVariable String challengeId,
            HttpServletRequest request) {
        try {
            // Extract Authorization header from request
            String authorizationHeader = request.getHeader("Authorization");
            
            // Extract userId from JWT token
            String userIdFromToken = jwtUtil.extractUserId(authorizationHeader);
            
            // Retrieve the challenge to verify ownership
            var getChallengeQuery = new GetChallengeByIdQuery(new ChallengeId(UUID.fromString(challengeId)));
            var challengeOptional = challengeQueryService.handle(getChallengeQuery);
            
            if (challengeOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Challenge not found with id: " + challengeId));
            }
            
            var challenge = challengeOptional.get();
            
            // Verify ownership: Only the teacher who created the challenge can delete it
            String challengeOwnerId = challenge.getTeacherId().id().toString();
            
            if (userIdFromToken == null || !userIdFromToken.equals(challengeOwnerId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResponse("You are not authorized to delete this challenge. Only the challenge owner can delete it."));
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

    // Add a guide to a challenge
    @PostMapping("/{challengeId}/guides/{guideId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    @Operation(summary = "Add guide to challenge", description = "Add a learning guide to a challenge. Only the challenge owner can add guides.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Guide added successfully"),
        @ApiResponse(responseCode = "403", description = "Forbidden - not the challenge owner"),
        @ApiResponse(responseCode = "404", description = "Challenge not found")
    })
    public ResponseEntity<?> addGuide(
            @PathVariable String challengeId,
            @PathVariable String guideId,
            HttpServletRequest request) {
        try {
            // Extract Authorization header from request
            String authorizationHeader = request.getHeader("Authorization");

            // Extract userId from JWT token
            String userIdFromToken = jwtUtil.extractUserId(authorizationHeader);

            // Retrieve the challenge to verify ownership
            var getChallengeQuery = new GetChallengeByIdQuery(new ChallengeId(UUID.fromString(challengeId)));
            var challengeOptional = challengeQueryService.handle(getChallengeQuery);

            if (challengeOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Challenge not found with id: " + challengeId));
            }

            var challenge = challengeOptional.get();

            // Verify ownership
            String challengeOwnerId = challenge.getTeacherId().id().toString();

            if (userIdFromToken == null || !userIdFromToken.equals(challengeOwnerId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResponse("You are not authorized to modify this challenge. Only the challenge owner can add guides."));
            }

            // Create command
            var command = new AddGuideCommand(
                new ChallengeId(UUID.fromString(challengeId)),
                UUID.fromString(guideId)
            );

            // Execute command
            challengeCommandService.handle(command);

            // Retrieve updated challenge for response
            var updatedChallenge = challengeQueryService.handle(getChallengeQuery);

            if (updatedChallenge.isPresent()) {
                // Fetch like data
                UUID challengeUuid = UUID.fromString(challengeId);
                UUID userUuid = UUID.fromString(userIdFromToken);
                
                boolean userLiked = challengeLikeRepository.existsByChallengeIdAndUserId(challengeUuid, userUuid);
                long likesCount = challengeLikeRepository.countByChallengeId(challengeUuid);
                
                var challengeResource = ChallengeResourceFromEntityAssembler.toResourceFromEntity(updatedChallenge.get(), userLiked, likesCount);
                return new ResponseEntity<>(challengeResource, HttpStatus.OK);
            }

            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("Invalid request: " + e.getMessage()));
        }
    }

    // Remove a guide from a challenge
    @DeleteMapping("/{challengeId}/guides/{guideId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    @Operation(summary = "Remove guide from challenge", description = "Remove a learning guide from a challenge. Only the challenge owner can remove guides.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Guide removed successfully"),
        @ApiResponse(responseCode = "403", description = "Forbidden - not the challenge owner"),
        @ApiResponse(responseCode = "404", description = "Challenge not found")
    })
    public ResponseEntity<?> removeGuide(
            @PathVariable String challengeId,
            @PathVariable String guideId,
            HttpServletRequest request) {
        try {
            // Extract Authorization header from request
            String authorizationHeader = request.getHeader("Authorization");

            // Extract userId from JWT token
            String userIdFromToken = jwtUtil.extractUserId(authorizationHeader);

            // Retrieve the challenge to verify ownership
            var getChallengeQuery = new GetChallengeByIdQuery(new ChallengeId(UUID.fromString(challengeId)));
            var challengeOptional = challengeQueryService.handle(getChallengeQuery);

            if (challengeOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Challenge not found with id: " + challengeId));
            }

            var challenge = challengeOptional.get();

            // Verify ownership
            String challengeOwnerId = challenge.getTeacherId().id().toString();

            if (userIdFromToken == null || !userIdFromToken.equals(challengeOwnerId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResponse("You are not authorized to modify this challenge. Only the challenge owner can remove guides."));
            }

            // Create command
            var command = new com.levelupjourney.microservicechallenges.challenges.domain.model.commands.RemoveGuideCommand(
                new ChallengeId(UUID.fromString(challengeId)),
                UUID.fromString(guideId)
            );

            // Execute command
            challengeCommandService.handle(command);

            // Retrieve updated challenge for response
            var updatedChallenge = challengeQueryService.handle(getChallengeQuery);

            if (updatedChallenge.isPresent()) {
                // Fetch like data
                UUID challengeUuid = UUID.fromString(challengeId);
                UUID userUuid = UUID.fromString(userIdFromToken);
                
                boolean userLiked = challengeLikeRepository.existsByChallengeIdAndUserId(challengeUuid, userUuid);
                long likesCount = challengeLikeRepository.countByChallengeId(challengeUuid);
                
                var challengeResource = ChallengeResourceFromEntityAssembler.toResourceFromEntity(updatedChallenge.get(), userLiked, likesCount);
                return new ResponseEntity<>(challengeResource, HttpStatus.OK);
            }

            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("Invalid request: " + e.getMessage()));
        }
    }

    // Like a challenge
    @PostMapping("/{challengeId}/likes")
    @Operation(summary = "Like a challenge", description = "Add a like to a challenge. Each user can like a challenge only once.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Like added successfully"),
        @ApiResponse(responseCode = "400", description = "Bad request - invalid challenge ID or user already liked"),
        @ApiResponse(responseCode = "404", description = "Challenge not found")
    })
    public ResponseEntity<?> likeChallenge(
            @PathVariable String challengeId,
            HttpServletRequest request) {
        try {
            // Extract user ID from JWT
            String authorizationHeader = request.getHeader("Authorization");
            String userId = jwtUtil.extractUserId(authorizationHeader);

            // Create and execute command
            var command = new LikeChallengeCommand(
                new ChallengeId(UUID.fromString(challengeId)),
                userId
            );
            challengeCommandService.handle(command);

            return ResponseEntity.status(HttpStatus.CREATED)
                .body(new MessageResponse("Challenge liked successfully"));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(e.getMessage()));
        }
    }

    // Unlike a challenge
    @DeleteMapping("/{challengeId}/likes")
    @Operation(summary = "Unlike a challenge", description = "Remove a like from a challenge.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Like removed successfully"),
        @ApiResponse(responseCode = "400", description = "Bad request - invalid challenge ID or user has not liked"),
        @ApiResponse(responseCode = "404", description = "Challenge not found")
    })
    public ResponseEntity<?> unlikeChallenge(
            @PathVariable String challengeId,
            HttpServletRequest request) {
        try {
            // Extract user ID from JWT
            String authorizationHeader = request.getHeader("Authorization");
            String userId = jwtUtil.extractUserId(authorizationHeader);

            // Create and execute command
            var command = new UnlikeChallengeCommand(
                new ChallengeId(UUID.fromString(challengeId)),
                userId
            );
            challengeCommandService.handle(command);

            return ResponseEntity.noContent().build();

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(e.getMessage()));
        }
    }

    // Simple response records
    private record MessageResponse(String message) {}
    private record ErrorResponse(String message) {}
}
