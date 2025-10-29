package com.levelupjourney.microservicechallenges.challenges.interfaces.rest;

import com.levelupjourney.microservicechallenges.challenges.domain.model.queries.GetCodeVersionByIdQuery;
import com.levelupjourney.microservicechallenges.challenges.domain.model.queries.GetCodeVersionsByChallengeIdQuery;
import com.levelupjourney.microservicechallenges.challenges.domain.model.queries.GetCodeVersionsByChallengeIdsQuery;
import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.ChallengeId;
import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.CodeVersionId;
import com.levelupjourney.microservicechallenges.challenges.domain.services.CodeVersionCommandService;
import com.levelupjourney.microservicechallenges.challenges.domain.services.CodeVersionQueryService;
import com.levelupjourney.microservicechallenges.challenges.interfaces.rest.resource.AddCodeVersionResource;
import com.levelupjourney.microservicechallenges.challenges.interfaces.rest.resource.UpdateCodeVersionResource;
import com.levelupjourney.microservicechallenges.challenges.interfaces.rest.transform.AddCodeVersionCommandFromResourceAssembler;
import com.levelupjourney.microservicechallenges.challenges.interfaces.rest.transform.CodeVersionResourceFromEntityAssembler;
import com.levelupjourney.microservicechallenges.challenges.interfaces.rest.transform.UpdateCodeVersionCommandFromResourceAssembler;
import com.levelupjourney.microservicechallenges.shared.infrastructure.security.JwtUtil;
import com.levelupjourney.microservicechallenges.solutions.interfaces.rest.resources.ErrorResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/v1/challenges/{challengeId}/code-versions", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Code Versions", description = "Endpoints for managing code versions of challenges")
@SecurityRequirement(name = "bearerAuth")
public class CodeVersionController {

    private final CodeVersionCommandService codeVersionCommandService;
    private final CodeVersionQueryService codeVersionQueryService;
    private final JwtUtil jwtUtil;

    public CodeVersionController(CodeVersionCommandService codeVersionCommandService,
                               CodeVersionQueryService codeVersionQueryService,
                               JwtUtil jwtUtil) {
        this.codeVersionCommandService = codeVersionCommandService;
        this.codeVersionQueryService = codeVersionQueryService;
        this.jwtUtil = jwtUtil;
    }

    // Create a new code version for a challenge
    @PostMapping
    @io.swagger.v3.oas.annotations.Operation(summary = "Create code version", description = "Create a new code version for a challenge with initial code and function name.")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Code version created successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Code version already exists for this language"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> createCodeVersion(@PathVariable String challengeId,
                                                                 @RequestBody AddCodeVersionResource resource,
                                                                 HttpServletRequest request) {
        // Extract user roles from JWT token - only teachers can create code versions
        String authorizationHeader = request.getHeader("Authorization");
        List<String> roles = jwtUtil.extractRoles(authorizationHeader);
        if (!roles.contains("ROLE_TEACHER") && !roles.contains("ROLE_ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResponse("Access denied. Only teachers and admins can create code versions."));
        }
        
        // Transform resource to domain command with challengeId from path (overriding path parameter)
        var resourceWithChallenge = new AddCodeVersionResource(challengeId, resource.language(), resource.defaultCode(), resource.functionName());
        var command = AddCodeVersionCommandFromResourceAssembler.toCommandFromResource(resourceWithChallenge);
        
        // Execute command through domain service
        var codeVersionId = codeVersionCommandService.handle(command);
        
        // Retrieve created code version for response
        var query = new GetCodeVersionByIdQuery(codeVersionId);
        var codeVersion = codeVersionQueryService.handle(query);
        
        // Transform domain entity to response resource
        if (codeVersion.isPresent()) {
            var codeVersionResource = CodeVersionResourceFromEntityAssembler.toResourceFromEntity(codeVersion.get());
            return new ResponseEntity<>(codeVersionResource, HttpStatus.CREATED);
        }
        
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Get code version by ID
    @GetMapping("/{codeVersionId}")
    @io.swagger.v3.oas.annotations.Operation(summary = "Get code version by ID", description = "Retrieve a specific code version including its function name.")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Code version retrieved successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Code version not found")
    })
    public ResponseEntity<?> getCodeVersionById(@PathVariable String challengeId,
                                                                  @PathVariable String codeVersionId,
                                                                  HttpServletRequest request) {
        // Extract user roles from JWT token - only teachers can access code versions
        String authorizationHeader = request.getHeader("Authorization");
        List<String> roles = jwtUtil.extractRoles(authorizationHeader);
        if (!roles.contains("ROLE_TEACHER") && !roles.contains("ROLE_ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResponse("Access denied. Only teachers and admins can access code versions."));
        }
        
        // Transform path variables to domain query
        var query = new GetCodeVersionByIdQuery(new CodeVersionId(UUID.fromString(codeVersionId)));
        
        // Execute query through domain service
        var codeVersion = codeVersionQueryService.handle(query);
        
        // Transform domain entity to response resource if found
        if (codeVersion.isPresent()) {
            var codeVersionResource = CodeVersionResourceFromEntityAssembler.toResourceFromEntity(codeVersion.get());
            return new ResponseEntity<>(codeVersionResource, HttpStatus.OK);
        }
        
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // Get all code versions for a challenge
    @GetMapping
    @io.swagger.v3.oas.annotations.Operation(summary = "Get all code versions", description = "Retrieve all code versions for a specific challenge.")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Code versions retrieved successfully")
    })
    public ResponseEntity<?> getCodeVersionsByChallenge(@PathVariable String challengeId,
                                                                                 HttpServletRequest request) {
        // Extract user roles from JWT token - only teachers can access code versions
        String authorizationHeader = request.getHeader("Authorization");
        List<String> roles = jwtUtil.extractRoles(authorizationHeader);
        if (!roles.contains("ROLE_TEACHER") && !roles.contains("ROLE_ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResponse("Access denied. Only teachers and admins can access code versions."));
        }
        
        // Transform path variable to domain query
        var query = new GetCodeVersionsByChallengeIdQuery(new ChallengeId(UUID.fromString(challengeId)));
        
        // Execute query through domain service
        var codeVersions = codeVersionQueryService.handle(query);
        
        // Transform domain entities to response resources
        var codeVersionResources = codeVersions.stream()
                .map(CodeVersionResourceFromEntityAssembler::toResourceFromEntity)
                .collect(Collectors.toList());
        
        return new ResponseEntity<>(codeVersionResources, HttpStatus.OK);
    }

    // Update code version content
    @PutMapping("/{codeVersionId}")
    @io.swagger.v3.oas.annotations.Operation(summary = "Update code version", description = "Update the initial code and/or function name of a code version.")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Code version updated successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Code version not found")
    })
    public ResponseEntity<?> updateCodeVersion(@PathVariable String challengeId,
                                                               @PathVariable String codeVersionId,
                                                               @RequestBody UpdateCodeVersionResource resource,
                                                               HttpServletRequest request) {
        // Extract user roles from JWT token - only teachers can update code versions
        String authorizationHeader = request.getHeader("Authorization");
        List<String> roles = jwtUtil.extractRoles(authorizationHeader);
        if (!roles.contains("ROLE_TEACHER") && !roles.contains("ROLE_ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResponse("Access denied. Only teachers and admins can update code versions."));
        }
        
        // Transform resource to domain command
        var command = UpdateCodeVersionCommandFromResourceAssembler.toCommandFromResource(codeVersionId, resource);
        
        // Execute command through domain service
        codeVersionCommandService.handle(command);
        
        // Retrieve updated code version for response
        var query = new GetCodeVersionByIdQuery(new CodeVersionId(UUID.fromString(codeVersionId)));
        var codeVersion = codeVersionQueryService.handle(query);
        
        // Transform domain entity to response resource
        if (codeVersion.isPresent()) {
            var codeVersionResource = CodeVersionResourceFromEntityAssembler.toResourceFromEntity(codeVersion.get());
            return new ResponseEntity<>(codeVersionResource, HttpStatus.OK);
        }
        
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /**
     * Batch endpoint: fetch code versions for multiple challenges in one request
     * Note: This endpoint ignores the {challengeId} path parameter and accepts multiple challenge IDs in the body
     */
    @PostMapping("/batch")
    @Operation(
        summary = "Get code versions for multiple challenges", 
        description = "Provide a list of challenge UUIDs in the request body and retrieve all code versions grouped by challenge. The {challengeId} path parameter is ignored for this endpoint. Only accessible by teachers and admins."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Code versions retrieved successfully. Returns a map where keys are challenge IDs and values are lists of code versions."
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Invalid request: null/empty challenge IDs or invalid UUID format"
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden - Only teachers and admins can access code versions"
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Internal server error"
        )
    })
    public ResponseEntity<?> getCodeVersionsForChallenges(
            @PathVariable String challengeId, // Path parameter required but ignored for batch operation
            @RequestBody List<String> challengeIds,
            HttpServletRequest request) {
        
        try {
            // Step 1: Authorization check - only teachers and admins
            String authorizationHeader = request.getHeader("Authorization");
            List<String> roles = jwtUtil.extractRoles(authorizationHeader);
            
            if (!roles.contains("ROLE_TEACHER") && !roles.contains("ROLE_ADMIN")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ErrorResponse("Access denied. Only teachers and admins can access code versions."));
            }

            // Step 2: Validate input
            if (challengeIds == null || challengeIds.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorResponse("challengeIds request body cannot be null or empty"));
            }

            // Step 3: Transform String IDs to domain value objects with validation
            List<ChallengeId> challengeIdVOs = challengeIds.stream()
                    .filter(id -> id != null && !id.isBlank())
                    .map(id -> {
                        try {
                            return new ChallengeId(UUID.fromString(id.trim()));
                        } catch (IllegalArgumentException e) {
                            throw new IllegalArgumentException("Invalid UUID format for challenge ID: " + id);
                        }
                    })
                    .toList();

            if (challengeIdVOs.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorResponse("No valid challenge IDs provided"));
            }

            // Step 4: Execute batch query using domain query service
            var query = new GetCodeVersionsByChallengeIdsQuery(challengeIdVOs);
            var codeVersions = codeVersionQueryService.handle(query);

            // Step 5: Group by challenge ID and transform to resources
            var grouped = codeVersions.stream()
                    .collect(Collectors.groupingBy(
                            cv -> cv.getChallengeId().id().toString(),
                            Collectors.mapping(
                                CodeVersionResourceFromEntityAssembler::toResourceFromEntity,
                                Collectors.toList()
                            )
                    ));

            // Step 6: Ensure all requested IDs are present in response (empty list if no versions found)
            for (ChallengeId cid : challengeIdVOs) {
                grouped.putIfAbsent(cid.id().toString(), List.of());
            }

            return ResponseEntity.ok(grouped);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Invalid UUID in request: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to fetch code versions: " + e.getMessage()));
        }
    }

}
