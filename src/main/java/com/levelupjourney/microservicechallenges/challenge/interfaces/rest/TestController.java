package com.levelupjourney.microservicechallenges.challenge.interfaces.rest;

import com.levelupjourney.microservicechallenges.challenge.domain.services.TestCommandService;
import com.levelupjourney.microservicechallenges.challenge.domain.services.TestQueryService;
import com.levelupjourney.microservicechallenges.challenge.interfaces.rest.resources.CreateTestResource;
import com.levelupjourney.microservicechallenges.challenge.interfaces.rest.resources.TestResource;
import com.levelupjourney.microservicechallenges.challenge.interfaces.rest.resources.UpdateTestResource;
import com.levelupjourney.microservicechallenges.challenge.interfaces.rest.transform.TestCommandFromResourceAssembler;
import com.levelupjourney.microservicechallenges.challenge.interfaces.rest.transform.TestQueryFromParametersAssembler;
import com.levelupjourney.microservicechallenges.challenge.interfaces.rest.transform.TestResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST Controller for Test aggregate
 * Handles test management within challenges
 */
@RestController
@RequestMapping("/api/v1/tests")
@CrossOrigin(origins = "*")
@Tag(name = "Tests", description = "Test case management operations")
public class TestController {

    private final TestCommandService testCommandService;
    private final TestQueryService testQueryService;

    public TestController(TestCommandService testCommandService,
                         TestQueryService testQueryService) {
        this.testCommandService = testCommandService;
        this.testQueryService = testQueryService;
    }

    // POST /api/v1/tests - Create new test
    @PostMapping
    @Operation(summary = "Create test case", description = "Creates a new test case for a challenge version")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Test case created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    public ResponseEntity<TestResource> createTest(
            @Parameter(description = "Challenge version ID") @RequestParam Long challengeVersionId,
            @RequestBody CreateTestResource resource) {
        try {
            var command = TestCommandFromResourceAssembler.toCommandFromResource(challengeVersionId, resource);
            var result = testCommandService.handle(command);
            
            if (result.isPresent()) {
                var testResource = TestResourceFromEntityAssembler.toResourceFromEntity(result.get());
                return new ResponseEntity<>(testResource, HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // GET /api/v1/tests - Get tests by challenge ID
    @GetMapping
    @Operation(summary = "Get tests by challenge", description = "Retrieves all test cases for a specific challenge")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Tests retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<TestResource>> getTestsByChallengeId(
            @Parameter(description = "Challenge unique identifier") @RequestParam String challengeId) {
        try {
            var query = TestQueryFromParametersAssembler.toGetTestsByChallengeIdQuery(challengeId);
            var tests = testQueryService.handle(query).stream()
                    .map(TestResourceFromEntityAssembler::toResourceFromEntity)
                    .collect(Collectors.toList());
            
            return new ResponseEntity<>(tests, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // GET /api/v1/tests/{testId} - Get test by ID
    @GetMapping("/{testId}")
    @Operation(summary = "Get test by ID", description = "Retrieves a specific test case by its identifier")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Test case found and retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Test case not found"),
        @ApiResponse(responseCode = "400", description = "Invalid test ID format")
    })
    public ResponseEntity<TestResource> getTestById(
            @Parameter(description = "Test case unique identifier") @PathVariable String testId) {
        try {
            var query = TestQueryFromParametersAssembler.toGetTestByIdQuery(testId);
            var result = testQueryService.handle(query);
            
            if (result.isPresent()) {
                var testResource = TestResourceFromEntityAssembler.toResourceFromEntity(result.get());
                return new ResponseEntity<>(testResource, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // PUT /api/v1/tests/{testId} - Update test
    @PutMapping("/{testId}")
    @Operation(summary = "Update test case", description = "Updates an existing test case with new input/output data")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Test case updated successfully"),
        @ApiResponse(responseCode = "404", description = "Test case not found"),
        @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    public ResponseEntity<TestResource> updateTest(
            @Parameter(description = "Test case unique identifier") @PathVariable String testId,
            @RequestBody UpdateTestResource resource) {
        try {
            var command = TestCommandFromResourceAssembler.toCommandFromResource(testId, resource);
            var result = testCommandService.handle(command);
            
            if (result.isPresent()) {
                var testResource = TestResourceFromEntityAssembler.toResourceFromEntity(result.get());
                return new ResponseEntity<>(testResource, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // DELETE /api/v1/tests/{testId} - Delete test
    @DeleteMapping("/{testId}")
    @Operation(summary = "Delete test case", description = "Permanently deletes a test case from the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Test case deleted successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid test ID or deletion failed")
    })
    public ResponseEntity<Void> deleteTest(
            @Parameter(description = "Test case unique identifier") @PathVariable String testId) {
        try {
            var command = TestCommandFromResourceAssembler.toDeleteCommandFromResource(testId);
            testCommandService.handle(command);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
