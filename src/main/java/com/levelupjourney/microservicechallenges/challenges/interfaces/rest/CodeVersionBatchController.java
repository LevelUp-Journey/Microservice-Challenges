package com.levelupjourney.microservicechallenges.challenges.interfaces.rest;

import com.levelupjourney.microservicechallenges.challenges.domain.model.queries.GetCodeVersionsByChallengeIdsQuery;
import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.ChallengeId;
import com.levelupjourney.microservicechallenges.challenges.domain.services.CodeVersionQueryService;
import com.levelupjourney.microservicechallenges.challenges.interfaces.rest.transform.CodeVersionResourceFromEntityAssembler;
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

/**
 * REST Controller for batch operations on code versions.
 * Handles endpoints that operate across multiple challenges.
 */
@RestController
@RequestMapping(value = "/api/v1/challenges/code-versions", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Code Versions - Batch", description = "Batch endpoints for code versions across multiple challenges")
@SecurityRequirement(name = "bearerAuth")
public class CodeVersionBatchController {

    private final CodeVersionQueryService codeVersionQueryService;
    private final JwtUtil jwtUtil;

    public CodeVersionBatchController(CodeVersionQueryService codeVersionQueryService,
                                     JwtUtil jwtUtil) {
        this.codeVersionQueryService = codeVersionQueryService;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Batch endpoint: fetch code versions for multiple challenges in one request.
     * URL: POST /api/v1/challenges/code-versions/batch
     * 
     * @param challengeIds List of challenge UUIDs to fetch code versions for
     * @param request HTTP request to extract authorization header
     * @return Map of challenge IDs to their code versions, grouped by challenge
     */
    @PostMapping("/batch")
    @Operation(
        summary = "Get code versions for multiple challenges", 
        description = "Provide a list of challenge UUIDs in the request body and retrieve all code versions grouped by challenge. Only accessible by teachers and admins."
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
