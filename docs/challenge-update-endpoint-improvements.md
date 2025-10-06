# Challenge Update Endpoint Improvements

## Overview
This document describes the improvements made to the challenge update endpoint to follow RESTful principles and add proper validation for status changes.

## Changes Made

### 1. REST API Improvements
- **Removed non-RESTful endpoint**: `/api/v1/challenges/{challengeId}/publish` (PATCH)
- **Updated existing endpoint**: `/api/v1/challenges/{challengeId}` (PATCH)
- Now handles all challenge updates including status changes through a single PATCH endpoint

### 2. UpdateChallengeResource
Added optional `status` field to support status updates:
```java
public record UpdateChallengeResource(
    Optional<String> name, 
    Optional<String> description, 
    Optional<Integer> experiencePoints, 
    Optional<String> status,  // NEW: Allows status updates
    List<String> tags
)
```

All fields are optional to support partial updates via PATCH.

### 3. UpdateChallengeCommand
Added optional `ChallengeStatus` field:
```java
public record UpdateChallengeCommand(
    ChallengeId challengeId, 
    Optional<String> name, 
    Optional<String> description, 
    Optional<Integer> experiencePoints, 
    Optional<ChallengeStatus> status,  // NEW
    Optional<List<Tag>> tags
)
```

### 4. Challenge Aggregate Improvements

#### New Validation Method
Added `canBePublished()` method to validate publication requirements:
```java
public boolean canBePublished() {
    // A challenge can be published if it has at least one CodeVersion
    // and that CodeVersion has at least 3 tests
    if (this.versions == null || this.versions.isEmpty()) {
        return false;
    }
    
    return this.versions.stream()
        .anyMatch(version -> version.getTests() != null && version.getTests().size() >= 3);
}
```

#### Enhanced updateDetails Method
Added overloaded method that accepts status changes:
```java
public void updateDetails(String name, String description, Integer experiencePoints, 
                         ChallengeStatus status, List<Tag> tags) {
    // ... update basic fields ...
    
    // Handle status change with validation
    if (status != null && status != this.status) {
        if (status == ChallengeStatus.PUBLISHED) {
            if (!canBePublished()) {
                throw new IllegalStateException(
                    "Cannot publish challenge: must have at least one code version with at least 3 tests");
            }
        }
        this.status = status;
    }
}
```

### 5. Service Layer Updates
Updated `ChallengeCommandServiceImpl.handle(UpdateChallengeCommand)` to pass status to the domain method:
```java
challenge.updateDetails(
    command.name().orElse(null),
    command.description().orElse(null),
    command.experiencePoints().orElse(null),
    command.status().orElse(null),  // NEW
    command.tags().orElse(null)
);
```

### 6. Controller Improvements
- Changed from `@PutMapping` to `@PatchMapping` for partial updates
- Added proper error handling:
  - `IllegalStateException` → HTTP 400 (Bad Request) - validation errors
  - `IllegalArgumentException` → HTTP 404 (Not Found) - invalid challenge ID

```java
@PatchMapping("/{challengeId}")
public ResponseEntity<ChallengeResource> updateChallenge(
        @PathVariable String challengeId,
        @RequestBody UpdateChallengeResource resource) {
    try {
        // ... handle update ...
    } catch (IllegalStateException e) {
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    } catch (IllegalArgumentException e) {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
```

### 7. Cleanup
Removed obsolete classes:
- `PublishChallengeCommand`
- `PublishChallengeResource`
- `PublishChallengeCommandFromResourceAssembler`
- Removed `handle(PublishChallengeCommand)` from `ChallengeCommandService` interface

## Business Rules

### Publishing Validation
A challenge can only be published (status changed to `PUBLISHED`) if:
1. It has at least **one CodeVersion**
2. That CodeVersion has at least **3 tests**

If these conditions are not met, the update will fail with HTTP 400 and an error message.

## API Usage Examples

### Update Challenge Name
```http
PATCH /api/v1/challenges/{challengeId}
Content-Type: application/json

{
  "name": "Updated Challenge Name"
}
```

### Publish a Challenge
```http
PATCH /api/v1/challenges/{challengeId}
Content-Type: application/json

{
  "status": "PUBLISHED"
}
```

### Update Multiple Fields Including Status
```http
PATCH /api/v1/challenges/{challengeId}
Content-Type: application/json

{
  "name": "Advanced Challenge",
  "description": "A challenging coding problem",
  "experiencePoints": 150,
  "status": "PUBLISHED"
}
```

## Error Responses

### 400 Bad Request
Returned when trying to publish a challenge that doesn't meet requirements:
```json
{
  "message": "Cannot publish challenge: must have at least one code version with at least 3 tests"
}
```

### 404 Not Found
Returned when the challenge ID doesn't exist.

## Migration Notes

For clients using the old `/api/v1/challenges/{challengeId}/publish` endpoint:
- **Old**: `PATCH /api/v1/challenges/{challengeId}/publish`
- **New**: `PATCH /api/v1/challenges/{challengeId}` with body `{"status": "PUBLISHED"}`

The old endpoint has been completely removed.
