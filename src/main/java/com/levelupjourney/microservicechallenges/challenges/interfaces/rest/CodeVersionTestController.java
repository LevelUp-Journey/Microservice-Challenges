package com.levelupjourney.microservicechallenges.challenges.interfaces.rest;

import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.CodeVersionId;
import com.levelupjourney.microservicechallenges.challenges.domain.services.CodeVersionTestCommandService;
import com.levelupjourney.microservicechallenges.challenges.domain.services.CodeVersionTestQueryService;
import com.levelupjourney.microservicechallenges.challenges.interfaces.rest.resource.AddCodeVersionTestResource;
import com.levelupjourney.microservicechallenges.challenges.interfaces.rest.resource.CodeVersionTestResource;
import com.levelupjourney.microservicechallenges.challenges.interfaces.rest.resource.UpdateCodeVersionTestResource;
import com.levelupjourney.microservicechallenges.challenges.interfaces.rest.transform.AddCodeVersionTestCommandFromResourceAssembler;
import com.levelupjourney.microservicechallenges.challenges.interfaces.rest.transform.CodeVersionTestResourceFromEntityAssembler;
import com.levelupjourney.microservicechallenges.challenges.interfaces.rest.transform.UpdateCodeVersionTestCommandFromResourceAssembler;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/v1/code-version-tests", produces = MediaType.APPLICATION_JSON_VALUE)
public class CodeVersionTestController {

    private final CodeVersionTestCommandService codeVersionTestCommandService;
    private final CodeVersionTestQueryService codeVersionTestQueryService;

    public CodeVersionTestController(CodeVersionTestCommandService codeVersionTestCommandService,
                                   CodeVersionTestQueryService codeVersionTestQueryService) {
        this.codeVersionTestCommandService = codeVersionTestCommandService;
        this.codeVersionTestQueryService = codeVersionTestQueryService;
    }

    // Add new test to a code version
    @PostMapping
    public ResponseEntity<CodeVersionTestResource> addCodeVersionTest(@RequestBody AddCodeVersionTestResource resource) {
        // Transform resource to domain command
        var command = AddCodeVersionTestCommandFromResourceAssembler.toCommandFromResource(resource);
        
        // Execute command through domain service
        var testId = codeVersionTestCommandService.handle(command);
        
        // Retrieve created test for response
        var test = codeVersionTestQueryService.getCodeVersionTestById(testId.value());
        
        // Transform domain entity to response resource
        if (test.isPresent()) {
            var testResource = CodeVersionTestResourceFromEntityAssembler.toResourceFromEntity(test.get());
            return new ResponseEntity<>(testResource, HttpStatus.CREATED);
        }
        
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Get test by ID
    @GetMapping("/{testId}")
    public ResponseEntity<CodeVersionTestResource> getCodeVersionTestById(@PathVariable String testId) {
        // Execute query through domain service
        var test = codeVersionTestQueryService.getCodeVersionTestById(UUID.fromString(testId));
        
        // Transform domain entity to response resource if found
        if (test.isPresent()) {
            var testResource = CodeVersionTestResourceFromEntityAssembler.toResourceFromEntity(test.get());
            return new ResponseEntity<>(testResource, HttpStatus.OK);
        }
        
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // Get all tests for a code version
    @GetMapping("/code-version/{codeVersionId}")
    public ResponseEntity<List<CodeVersionTestResource>> getTestsByCodeVersion(@PathVariable String codeVersionId) {
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
    public ResponseEntity<CodeVersionTestResource> updateCodeVersionTest(@PathVariable String testId,
                                                                       @RequestBody UpdateCodeVersionTestResource resource) {
        // Transform resource to domain command
        var command = UpdateCodeVersionTestCommandFromResourceAssembler.toCommandFromResource(testId, resource);
        
        // Execute command through domain service
        codeVersionTestCommandService.handle(command);
        
        // Retrieve updated test for response
        var test = codeVersionTestQueryService.getCodeVersionTestById(UUID.fromString(testId));
        
        // Transform domain entity to response resource
        if (test.isPresent()) {
            var testResource = CodeVersionTestResourceFromEntityAssembler.toResourceFromEntity(test.get());
            return new ResponseEntity<>(testResource, HttpStatus.OK);
        }
        
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // Get test count for a code version
    @GetMapping("/code-version/{codeVersionId}/count")
    public ResponseEntity<Long> getTestCountByCodeVersion(@PathVariable String codeVersionId) {
        // Execute query through domain service
        var count = codeVersionTestQueryService.countTestsByCodeVersionId(
                new CodeVersionId(UUID.fromString(codeVersionId)));
        
        return new ResponseEntity<>(count, HttpStatus.OK);
    }
}