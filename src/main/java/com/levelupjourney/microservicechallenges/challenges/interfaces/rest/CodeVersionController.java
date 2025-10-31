package com.levelupjourney.microservicechallenges.challenges.interfaces.rest;

import com.levelupjourney.microservicechallenges.challenges.domain.model.queries.GetCodeVersionByIdQuery;
import com.levelupjourney.microservicechallenges.challenges.domain.model.queries.GetCodeVersionsByChallengeIdQuery;
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
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Bad request - challenge not found or code version already exists for this language"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> createCodeVersion(@PathVariable String challengeId,
                                                                 @RequestBody AddCodeVersionResource resource,
                                                                 HttpServletRequest request) {
        try {
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
        } catch (IllegalArgumentException e) {
            // Handle validation errors (challenge not found, code version already exists)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("Validation error: " + e.getMessage()));
        }
    }

    // Get code version by ID
    @GetMapping("/{codeVersionId}")
    @Operation(summary = "Get code version by ID", description = "Retrieve a specific code version including its function name.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Code version retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Code version not found")
    })
    public ResponseEntity<?> getCodeVersionById(@PathVariable String challengeId,
                                                                  @PathVariable String codeVersionId,
                                                                  HttpServletRequest request) {
        // Extract user roles from JWT token - only teachers can access code versions
        String authorizationHeader = request.getHeader("Authorization");
        List<String> roles = jwtUtil.extractRoles(authorizationHeader);
//        if (!roles.contains("ROLE_TEACHER") && !roles.contains("ROLE_ADMIN")) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                    .body(new ErrorResponse("Access denied. Only teachers and admins can access code versions."));
//        }
        
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
    @Operation(summary = "Get all code versions", description = "Retrieve all code versions for a specific challenge.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Code versions retrieved successfully")
    })
    public ResponseEntity<?> getCodeVersionsByChallenge(@PathVariable String challengeId,
                                                                                 HttpServletRequest request) {
        // Extract user roles from JWT token - only teachers can access code versions
        String authorizationHeader = request.getHeader("Authorization");
        List<String> roles = jwtUtil.extractRoles(authorizationHeader);
//        if (!roles.contains("ROLE_TEACHER") && !roles.contains("ROLE_ADMIN")) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                    .body(new ErrorResponse("Access denied. Only teachers and admins can access code versions."));
//        }
        
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
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Bad request - code version not found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Code version not found")
    })
    public ResponseEntity<?> updateCodeVersion(@PathVariable String challengeId,
                                                               @PathVariable String codeVersionId,
                                                               @RequestBody UpdateCodeVersionResource resource,
                                                               HttpServletRequest request) {
        try {
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
        } catch (IllegalArgumentException e) {
            // Handle validation errors (code version not found)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("Validation error: " + e.getMessage()));
        }
    }

}
