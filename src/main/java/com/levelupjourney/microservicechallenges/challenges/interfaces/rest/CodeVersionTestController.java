package com.levelupjourney.microservicechallenges.challenges.interfaces.rest;

import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.CodeVersionId;
import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.CodeVersionTestId;
import com.levelupjourney.microservicechallenges.challenges.domain.services.CodeVersionTestCommandService;
import com.levelupjourney.microservicechallenges.challenges.domain.services.CodeVersionTestQueryService;
import com.levelupjourney.microservicechallenges.challenges.interfaces.rest.resource.AddCodeVersionTestResource;
import com.levelupjourney.microservicechallenges.challenges.interfaces.rest.resource.CodeVersionTestResource;
import com.levelupjourney.microservicechallenges.challenges.interfaces.rest.resource.UpdateCodeVersionTestResource;
import com.levelupjourney.microservicechallenges.challenges.interfaces.rest.transform.AddCodeVersionTestCommandFromResourceAssembler;
import com.levelupjourney.microservicechallenges.challenges.interfaces.rest.transform.CodeVersionTestResourceFromEntityAssembler;
import com.levelupjourney.microservicechallenges.challenges.interfaces.rest.transform.UpdateCodeVersionTestCommandFromResourceAssembler;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/v1/challenges/{challengeId}/code-versions/{codeVersionId}/tests", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Code Version Tests", description = "Endpoints for managing tests associated with code versions")
public class CodeVersionTestController {

    private final CodeVersionTestCommandService codeVersionTestCommandService;
    private final CodeVersionTestQueryService codeVersionTestQueryService;

    public CodeVersionTestController(CodeVersionTestCommandService codeVersionTestCommandService,
                                   CodeVersionTestQueryService codeVersionTestQueryService) {
        this.codeVersionTestCommandService = codeVersionTestCommandService;
        this.codeVersionTestQueryService = codeVersionTestQueryService;
    }

    // Create a new test for a code version
    @PostMapping
    @io.swagger.v3.oas.annotations.Operation(summary = "Create test", description = "Create a new test for a code version. isSecret indicates if the test is hidden from students.")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Test created successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<CodeVersionTestResource> addCodeVersionTest(@PathVariable String challengeId,
                                                                      @PathVariable String codeVersionId,
                                                                      @RequestBody AddCodeVersionTestResource resource) {
        // Transform resource to domain command with codeVersionId from path (overriding path parameter)
        var resourceWithCodeVersion = new AddCodeVersionTestResource(codeVersionId, resource.input(), 
                                                                     resource.expectedOutput(), 
                                                                     resource.customValidationCode(), 
                                                                     resource.failureMessage(),
                                                                     resource.isSecret());
        var command = AddCodeVersionTestCommandFromResourceAssembler.toCommandFromResource(resourceWithCodeVersion);
        
        // Execute command through domain service
        var testId = codeVersionTestCommandService.handle(command);
        
        // Retrieve created test for response
        var test = codeVersionTestQueryService.getCodeVersionTestById(testId);
        
        // Transform domain entity to response resource
        if (test.isPresent()) {
            var testResource = CodeVersionTestResourceFromEntityAssembler.toResourceFromEntity(test.get());
            return new ResponseEntity<>(testResource, HttpStatus.CREATED);
        }
        
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Get test by ID
    @GetMapping("/{testId}")
    @io.swagger.v3.oas.annotations.Operation(summary = "Get test by ID", description = "Retrieve a specific test including its secret status.")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Test retrieved successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Test not found")
    })
    public ResponseEntity<CodeVersionTestResource> getCodeVersionTestById(@PathVariable String challengeId,
                                                                          @PathVariable String codeVersionId,
                                                                          @PathVariable String testId) {
        // Execute query through domain service
        var test = codeVersionTestQueryService.getCodeVersionTestById(new CodeVersionTestId(UUID.fromString(testId)));
        
        // Transform domain entity to response resource if found
        if (test.isPresent()) {
            var testResource = CodeVersionTestResourceFromEntityAssembler.toResourceFromEntity(test.get());
            return new ResponseEntity<>(testResource, HttpStatus.OK);
        }
        
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // Get all tests for a code version
    @GetMapping
    @io.swagger.v3.oas.annotations.Operation(summary = "Get all tests", description = "Retrieve all tests for a code version, including secret tests.")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Tests retrieved successfully")
    })
    public ResponseEntity<List<CodeVersionTestResource>> getTestsByCodeVersion(@PathVariable String challengeId,
                                                                               @PathVariable String codeVersionId) {
        // Execute query through domain service
        var tests = codeVersionTestQueryService.getCodeVersionTestsByCodeVersionId(
                new CodeVersionId(UUID.fromString(codeVersionId)));
        
        // Transform domain entities to response resources
        var testResources = tests.stream()
                .map(CodeVersionTestResourceFromEntityAssembler::toResourceFromEntity)
                .collect(Collectors.toList());
        
        return new ResponseEntity<>(testResources, HttpStatus.OK);
    }

    // Update test content
    @PutMapping("/{testId}")
    @io.swagger.v3.oas.annotations.Operation(summary = "Update test", description = "Update test details including input, expected output, custom validation, failure message, and secret status.")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Test updated successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Test not found")
    })
    public ResponseEntity<CodeVersionTestResource> updateCodeVersionTest(@PathVariable String challengeId,
                                                                         @PathVariable String codeVersionId,
                                                                         @PathVariable String testId,
                                                                         @RequestBody UpdateCodeVersionTestResource resource) {
        // Transform resource to domain command
        var command = UpdateCodeVersionTestCommandFromResourceAssembler.toCommandFromResource(testId, resource);
        
        // Execute command through domain service
        codeVersionTestCommandService.handle(command);
        
        // Retrieve updated test for response
        var test = codeVersionTestQueryService.getCodeVersionTestById(new CodeVersionTestId(UUID.fromString(testId)));
        
        // Transform domain entity to response resource
        if (test.isPresent()) {
            var testResource = CodeVersionTestResourceFromEntityAssembler.toResourceFromEntity(test.get());
            return new ResponseEntity<>(testResource, HttpStatus.OK);
        }
        
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}