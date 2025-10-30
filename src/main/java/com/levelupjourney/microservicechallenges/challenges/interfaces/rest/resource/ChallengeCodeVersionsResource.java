package com.levelupjourney.microservicechallenges.challenges.interfaces.rest.resource;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * Resource representing code versions grouped by challenge for batch operations.
 */
@Schema(
    name = "ChallengeCodeVersionsResource",
    description = "A challenge and its associated code versions"
)
public record ChallengeCodeVersionsResource(
    @Schema(
        description = "UUID of the challenge",
        example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
        required = true
    )
    String challengeId,
    
    @Schema(
        description = "List of code versions for this challenge. Empty array if no code versions exist.",
        required = true
    )
    List<CodeVersionWithoutTestsResource> codeVersions
) {
}
