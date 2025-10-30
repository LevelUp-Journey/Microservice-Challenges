package com.levelupjourney.microservicechallenges.challenges.interfaces.rest;

import com.levelupjourney.microservicechallenges.challenges.domain.model.queries.GetCodeVersionsByChallengeIdsQuery;
import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.ChallengeId;
import com.levelupjourney.microservicechallenges.challenges.domain.services.CodeVersionQueryService;
import com.levelupjourney.microservicechallenges.challenges.interfaces.rest.resource.BatchCodeVersionsRequest;
import com.levelupjourney.microservicechallenges.challenges.interfaces.rest.resource.ChallengeCodeVersionsResource;
import com.levelupjourney.microservicechallenges.challenges.interfaces.rest.resource.CodeVersionWithoutTestsResource;
import com.levelupjourney.microservicechallenges.solutions.interfaces.rest.resources.ErrorResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * REST Controller for batch operations on code versions.
 * Handles endpoints that operate across multiple challenges.
 */
@RestController
@RequestMapping(value = "/api/v1/challenges/code-versions", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Code Versions", description = "Endpoints for managing code versions of challenges")
public class CodeVersionBatchController {

    private final CodeVersionQueryService codeVersionQueryService;

    public CodeVersionBatchController(CodeVersionQueryService codeVersionQueryService) {
        this.codeVersionQueryService = codeVersionQueryService;
    }

    @PostMapping("/batch")
    @Operation(
        summary = "Get code versions for multiple challenges", 
        description = """
            Fetch code versions for multiple challenges in a single request.
            
            **Authorization:** Public endpoint - accessible to everyone.
            
            **Request Body:** Object with challengeIds array
            
            **Response:** Array of objects, each containing:
            - `challengeId`: The UUID of the challenge
            - `codeVersions`: Array of code versions for that challenge (WITHOUT tests)
            
            **Use Cases:**
            - Bulk loading code versions for multiple challenges
            - Reducing API calls when displaying multiple challenges
            """
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Code versions retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                array = @ArraySchema(
                    schema = @Schema(implementation = ChallengeCodeVersionsResource.class)
                ),
                examples = @ExampleObject(
                    name = "Success Response",
                    value = """
                        [
                          {
                            "challengeId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
                            "codeVersions": [
                              {
                                "id": "cv1-uuid",
                                "challengeId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
                                "language": "javascript",
                                "initialCode": "function solve() { }",
                                "functionName": "solve"
                              },
                              {
                                "id": "cv2-uuid",
                                "challengeId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
                                "language": "python",
                                "initialCode": "def solve():\\n    pass",
                                "functionName": "solve"
                              }
                            ]
                          },
                          {
                            "challengeId": "b2c3d4e5-f6a7-8901-bcde-f12345678901",
                            "codeVersions": []
                          }
                        ]
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Bad Request - Invalid input",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Invalid Request",
                    value = """
                        {
                          "error": "challengeIds cannot be null or empty"
                        }
                        """
                )
            )
        )
    })
    public ResponseEntity<?> getCodeVersionsForChallenges(
            @RequestBody BatchCodeVersionsRequest request) {
        
        try {
            // Validate input
            if (request == null || request.challengeIds() == null || request.challengeIds().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorResponse("challengeIds cannot be null or empty"));
            }

            // Transform String IDs to domain value objects
            List<ChallengeId> challengeIdVOs = request.challengeIds().stream()
                    .filter(id -> id != null && !id.isBlank())
                    .map(id -> {
                        try {
                            return new ChallengeId(UUID.fromString(id.trim()));
                        } catch (IllegalArgumentException e) {
                            throw new IllegalArgumentException("Invalid UUID format: " + id);
                        }
                    })
                    .toList();

            if (challengeIdVOs.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorResponse("No valid challenge IDs provided"));
            }

            // Execute batch query
            var query = new GetCodeVersionsByChallengeIdsQuery(challengeIdVOs);
            var codeVersions = codeVersionQueryService.handle(query);

            // Group by challenge ID and transform WITHOUT tests
            Map<String, List<CodeVersionWithoutTestsResource>> groupedByChallenge = codeVersions.stream()
                    .collect(Collectors.groupingBy(
                            cv -> cv.getChallengeId().id().toString(),
                            Collectors.mapping(
                                cv -> new CodeVersionWithoutTestsResource(
                                    cv.getId().id().toString(),
                                    cv.getChallengeId().id().toString(),
                                    cv.getLanguage().name(),
                                    cv.getInitialCode(),
                                    cv.getFunctionName()
                                ),
                                Collectors.toList()
                            )
                    ));

            // Build response maintaining order
            List<ChallengeCodeVersionsResource> response = new ArrayList<>();
            for (ChallengeId cid : challengeIdVOs) {
                String challengeIdStr = cid.id().toString();
                List<CodeVersionWithoutTestsResource> versions = 
                    groupedByChallenge.getOrDefault(challengeIdStr, List.of());
                
                response.add(new ChallengeCodeVersionsResource(challengeIdStr, versions));
            }

            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Invalid UUID: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to fetch code versions: " + e.getMessage()));
        }
    }
}
