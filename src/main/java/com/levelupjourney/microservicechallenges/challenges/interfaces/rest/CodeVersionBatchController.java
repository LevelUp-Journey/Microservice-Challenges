package com.levelupjourney.microservicechallenges.challenges.interfaces.rest;

import com.levelupjourney.microservicechallenges.challenges.domain.model.queries.GetCodeVersionsByChallengeIdsQuery;
import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.ChallengeId;
import com.levelupjourney.microservicechallenges.challenges.domain.services.CodeVersionQueryService;
import com.levelupjourney.microservicechallenges.challenges.interfaces.rest.resource.ChallengeCodeVersionsResource;
import com.levelupjourney.microservicechallenges.challenges.interfaces.rest.transform.CodeVersionResourceFromEntityAssembler;
import com.levelupjourney.microservicechallenges.shared.infrastructure.security.JwtUtil;
import com.levelupjourney.microservicechallenges.solutions.interfaces.rest.resources.ErrorResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
     * @return List of ChallengeCodeVersionsResource objects, each containing a challengeId and its code versions
     */
    @PostMapping("/batch")
    @Operation(
        summary = "Get code versions for multiple challenges", 
        description = """
            Fetch code versions for multiple challenges in a single request.
            
            **Authorization:** Public endpoint - accessible to everyone (students, teachers, admins, anonymous users).
            
            **Test Visibility Rules:**
            - **Students**: Secret tests have empty strings ("") for input, expectedOutput, customValidationCode, and failureMessage
            - **Teachers/Admins**: Full access to all test details including secret tests
            - **Anonymous users**: Treated as students (secret test details hidden)
            
            **Request Body:** Array of challenge UUIDs (strings)
            
            **Response:** Array of objects, each containing:
            - `challengeId`: The UUID of the challenge
            - `codeVersions`: Array of code versions for that challenge (empty if none exist)
              - Each code version includes its `tests` array
              - Secret tests are filtered based on user role
            
            **Use Cases:**
            - Bulk loading code versions for multiple challenges
            - Students viewing available challenges and their test cases
            - Teachers reviewing all challenge code versions with full test details
            - Reducing API calls when displaying multiple challenges with their code versions
            """,
        requestBody = @RequestBody(
            description = "List of challenge UUIDs to fetch code versions for",
            required = true,
            content = @Content(
                mediaType = "application/json",
                array = @ArraySchema(
                    schema = @Schema(
                        type = "string",
                        format = "uuid",
                        example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890"
                    )
                ),
                examples = @ExampleObject(
                    name = "Example Request",
                    value = """
                        [
                          "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
                          "b2c3d4e5-f6a7-8901-bcde-f12345678901",
                          "c3d4e5f6-a7b8-9012-cdef-012345678901"
                        ]
                        """,
                    summary = "List of 3 challenge UUIDs"
                )
            )
        )
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Code versions retrieved successfully. Test details are filtered based on user role.",
            content = @Content(
                mediaType = "application/json",
                array = @ArraySchema(
                    schema = @Schema(implementation = ChallengeCodeVersionsResource.class)
                ),
                examples = {
                    @ExampleObject(
                        name = "Teacher/Admin Response",
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
                                    "functionName": "solve",
                                    "tests": [
                                      {
                                        "id": "test1-uuid",
                                        "codeVersionId": "cv1-uuid",
                                        "input": "5",
                                        "expectedOutput": "10",
                                        "customValidationCode": "",
                                        "failureMessage": "Failed test",
                                        "isSecret": false
                                      },
                                      {
                                        "id": "test2-uuid",
                                        "codeVersionId": "cv1-uuid",
                                        "input": "100",
                                        "expectedOutput": "200",
                                        "customValidationCode": "custom code",
                                        "failureMessage": "Secret test failed",
                                        "isSecret": true
                                      }
                                    ]
                                  }
                                ]
                              }
                            ]
                            """,
                        summary = "Full test details visible to teachers/admins"
                    ),
                    @ExampleObject(
                        name = "Student Response",
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
                                    "functionName": "solve",
                                    "tests": [
                                      {
                                        "id": "test1-uuid",
                                        "codeVersionId": "cv1-uuid",
                                        "input": "5",
                                        "expectedOutput": "10",
                                        "customValidationCode": "",
                                        "failureMessage": "Failed test",
                                        "isSecret": false
                                      },
                                      {
                                        "id": "test2-uuid",
                                        "codeVersionId": "cv1-uuid",
                                        "input": "",
                                        "expectedOutput": "",
                                        "customValidationCode": "",
                                        "failureMessage": "",
                                        "isSecret": true
                                      }
                                    ]
                                  }
                                ]
                              }
                            ]
                            """,
                        summary = "Secret test details hidden for students"
                    )
                }
            )
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Bad Request - Invalid or empty challenge IDs",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = {
                    @ExampleObject(
                        name = "Null or Empty Body",
                        value = """
                            {
                              "error": "challengeIds request body cannot be null or empty"
                            }
                            """,
                        summary = "Missing request body"
                    ),
                    @ExampleObject(
                        name = "Invalid UUID Format",
                        value = """
                            {
                              "error": "Invalid UUID in request: Invalid UUID format for challenge ID: invalid-uuid"
                            }
                            """,
                        summary = "Malformed UUID"
                    ),
                    @ExampleObject(
                        name = "No Valid IDs",
                        value = """
                            {
                              "error": "No valid challenge IDs provided"
                            }
                            """,
                        summary = "All IDs were null or blank"
                    )
                }
            )
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Internal Server Error",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(
                    name = "Server Error",
                    value = """
                        {
                          "error": "Failed to fetch code versions: Database connection error"
                        }
                        """,
                    summary = "Unexpected server error"
                )
            )
        )
    })
    public ResponseEntity<?> getCodeVersionsForChallenges(
            @org.springframework.web.bind.annotation.RequestBody List<String> challengeIds,
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        
        try {
            // Step 1: Determine if user is student (for test filtering)
            boolean isStudent = true; // Default to student (most restrictive)
            
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                try {
                    List<String> roles = jwtUtil.extractRoles(authorizationHeader);
                    // If user has TEACHER or ADMIN role, they see everything
                    isStudent = !roles.contains("ROLE_TEACHER") && !roles.contains("ROLE_ADMIN");
                } catch (Exception e) {
                    // Invalid token - treat as student
                    isStudent = true;
                }
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

            // Step 5: Group by challenge ID with role-based filtering
            final boolean isStudentFinal = isStudent;
            Map<String, List<com.levelupjourney.microservicechallenges.challenges.interfaces.rest.resource.CodeVersionResource>> groupedByChallenge = codeVersions.stream()
                    .collect(Collectors.groupingBy(
                            cv -> cv.getChallengeId().id().toString(),
                            Collectors.mapping(
                                cv -> CodeVersionResourceFromEntityAssembler.toResourceFromEntity(cv, isStudentFinal),
                                Collectors.toList()
                            )
                    ));

            // Step 6: Build response array maintaining the order of requested IDs
            List<ChallengeCodeVersionsResource> response = new ArrayList<>();
            for (ChallengeId cid : challengeIdVOs) {
                String challengeIdStr = cid.id().toString();
                List<com.levelupjourney.microservicechallenges.challenges.interfaces.rest.resource.CodeVersionResource> versions = 
                    groupedByChallenge.getOrDefault(challengeIdStr, List.of());
                
                response.add(new ChallengeCodeVersionsResource(challengeIdStr, versions));
            }

            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Invalid UUID in request: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to fetch code versions: " + e.getMessage()));
        }
    }
}
