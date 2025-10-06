package com.levelupjourney.microservicechallenges.challenges.interfaces.rest;

import com.levelupjourney.microservicechallenges.challenges.domain.model.queries.GetAllTagsQuery;
import com.levelupjourney.microservicechallenges.challenges.domain.model.queries.GetTagByIdQuery;
import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.TagId;
import com.levelupjourney.microservicechallenges.challenges.domain.services.TagCommandService;
import com.levelupjourney.microservicechallenges.challenges.domain.services.TagQueryService;
import com.levelupjourney.microservicechallenges.challenges.interfaces.rest.resource.*;
import com.levelupjourney.microservicechallenges.challenges.interfaces.rest.transform.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/v1/tags", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Tags", description = "Endpoints for managing independent tags")
public class TagController {

    private final TagCommandService tagCommandService;
    private final TagQueryService tagQueryService;

    public TagController(TagCommandService tagCommandService,
                        TagQueryService tagQueryService) {
        this.tagCommandService = tagCommandService;
        this.tagQueryService = tagQueryService;
    }

    // Get all tags
    @GetMapping
    @Operation(summary = "Get all tags", description = "Retrieve all available tags in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved all tags")
    })
    public ResponseEntity<List<TagResource>> getAllTags() {
        // Execute query through domain service
        var query = new GetAllTagsQuery();
        var tags = tagQueryService.handle(query);

        // Transform domain entities to response resources
        var tagResources = tags.stream()
                .map(TagResourceFromEntityAssembler::toResourceFromEntity)
                .collect(Collectors.toList());

        return new ResponseEntity<>(tagResources, HttpStatus.OK);
    }

    // Get tag by ID
    @GetMapping("/{tagId}")
    @Operation(summary = "Get tag by ID", description = "Retrieve a specific tag by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved tag"),
        @ApiResponse(responseCode = "404", description = "Tag not found"),
        @ApiResponse(responseCode = "400", description = "Invalid tag ID format")
    })
    public ResponseEntity<TagResource> getTagById(@PathVariable String tagId) {
        try {
            // Execute query through domain service
            var query = new GetTagByIdQuery(new TagId(UUID.fromString(tagId)));
            var tag = tagQueryService.handle(query);

            if (tag.isPresent()) {
                var tagResource = TagResourceFromEntityAssembler.toResourceFromEntity(tag.get());
                return new ResponseEntity<>(tagResource, HttpStatus.OK);
            }

            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // Create a new tag
    @PostMapping
    @Operation(summary = "Create tag", description = "Create a new independent tag")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Tag successfully created"),
        @ApiResponse(responseCode = "400", description = "Invalid request or tag already exists")
    })
    public ResponseEntity<TagResource> createTag(@RequestBody CreateTagResource resource) {
        try {
            // Transform resource to domain command
            var command = CreateTagCommandFromResourceAssembler.toCommandFromResource(resource);

            // Execute command through domain service
            var createdTagId = tagCommandService.handle(command);

            // Retrieve created tag for response
            var query = new GetTagByIdQuery(createdTagId);
            var tag = tagQueryService.handle(query);

            if (tag.isPresent()) {
                var tagResource = TagResourceFromEntityAssembler.toResourceFromEntity(tag.get());
                return new ResponseEntity<>(tagResource, HttpStatus.CREATED);
            }

            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // Update an existing tag
    @PutMapping("/{tagId}")
    @Operation(summary = "Update tag", description = "Update an existing tag")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Tag successfully updated"),
        @ApiResponse(responseCode = "404", description = "Tag not found"),
        @ApiResponse(responseCode = "400", description = "Invalid request or tag name already exists")
    })
    public ResponseEntity<TagResource> updateTag(@PathVariable String tagId,
                                               @RequestBody UpdateTagResource resource) {
        try {
            // Transform resource to domain command
            var command = UpdateTagCommandFromResourceAssembler.toCommandFromResource(tagId, resource);

            // Execute command through domain service
            tagCommandService.handle(command);

            // Retrieve updated tag for response
            var query = new GetTagByIdQuery(new TagId(UUID.fromString(tagId)));
            var tag = tagQueryService.handle(query);

            if (tag.isPresent()) {
                var tagResource = TagResourceFromEntityAssembler.toResourceFromEntity(tag.get());
                return new ResponseEntity<>(tagResource, HttpStatus.OK);
            }

            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // Delete a tag
    @DeleteMapping("/{tagId}")
    @Operation(summary = "Delete tag", description = "Delete an existing tag")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Tag successfully deleted"),
        @ApiResponse(responseCode = "404", description = "Tag not found"),
        @ApiResponse(responseCode = "400", description = "Invalid tag ID format")
    })
    public ResponseEntity<Void> deleteTag(@PathVariable String tagId) {
        try {
            // Transform path variable to domain command
            var command = DeleteTagCommandFromResourceAssembler.toCommandFromResource(tagId);

            // Execute command through domain service
            tagCommandService.handle(command);

            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}