package com.levelupjourney.microservicechallenges.challenges.interfaces.rest;

import com.levelupjourney.microservicechallenges.challenges.domain.model.queries.GetCodeVersionByIdQuery;
import com.levelupjourney.microservicechallenges.challenges.domain.model.queries.GetCodeVersionsByChallengeIdQuery;
import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.ChallengeId;
import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.CodeVersionId;
import com.levelupjourney.microservicechallenges.challenges.domain.services.CodeVersionCommandService;
import com.levelupjourney.microservicechallenges.challenges.domain.services.CodeVersionQueryService;
import com.levelupjourney.microservicechallenges.challenges.interfaces.rest.resource.AddCodeVersionResource;
import com.levelupjourney.microservicechallenges.challenges.interfaces.rest.resource.CodeVersionResource;
import com.levelupjourney.microservicechallenges.challenges.interfaces.rest.resource.UpdateCodeVersionResource;
import com.levelupjourney.microservicechallenges.challenges.interfaces.rest.transform.AddCodeVersionCommandFromResourceAssembler;
import com.levelupjourney.microservicechallenges.challenges.interfaces.rest.transform.CodeVersionResourceFromEntityAssembler;
import com.levelupjourney.microservicechallenges.challenges.interfaces.rest.transform.UpdateCodeVersionCommandFromResourceAssembler;
import io.swagger.v3.oas.annotations.tags.Tag;
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
public class CodeVersionController {

    private final CodeVersionCommandService codeVersionCommandService;
    private final CodeVersionQueryService codeVersionQueryService;

    public CodeVersionController(CodeVersionCommandService codeVersionCommandService,
                               CodeVersionQueryService codeVersionQueryService) {
        this.codeVersionCommandService = codeVersionCommandService;
        this.codeVersionQueryService = codeVersionQueryService;
    }

    // Create a new code version for a challenge
    @PostMapping
    @io.swagger.v3.oas.annotations.Operation(summary = "Create code version", description = "Create a new code version for a challenge with initial code and function name.")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Code version created successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Code version already exists for this language"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<CodeVersionResource> createCodeVersion(@PathVariable String challengeId,
                                                                 @RequestBody AddCodeVersionResource resource) {
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
    public ResponseEntity<CodeVersionResource> getCodeVersionById(@PathVariable String challengeId,
                                                                  @PathVariable String codeVersionId) {
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
    public ResponseEntity<List<CodeVersionResource>> getCodeVersionsByChallenge(@PathVariable String challengeId) {
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
    public ResponseEntity<CodeVersionResource> updateCodeVersion(@PathVariable String challengeId,
                                                               @PathVariable String codeVersionId,
                                                               @RequestBody UpdateCodeVersionResource resource) {
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
}
