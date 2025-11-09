# Challenge Guides Feature Implementation Summary

## Overview
Successfully implemented a new feature to add learning guides to challenges in the Microservice-Challenges application. Teachers can now associate guides with challenges and set a maximum number of attempts before showing these guides to students.

## Changes Made

### 1. Domain Model Updates

#### Challenge.java (Domain Aggregate)
- **New Fields:**
  - `guides`: List<UUID> - Collection of guide IDs associated with the challenge
  - `maxAttemptsBeforeGuides`: Integer - Maximum attempts before guides are shown to students

- **New Business Methods:**
  - `addGuide(UUID guideId)`: Add a guide to the challenge (prevents duplicates)
  - `removeGuide(UUID guideId)`: Remove a guide from the challenge
  - `hasGuide(UUID guideId)`: Check if a guide exists in the challenge
  - `updateMaxAttemptsBeforeGuides(Integer maxAttempts)`: Update the max attempts value

- **Database Schema:**
  - New table: `challenge_guides` (junction table for guide IDs)
  - New column: `max_attempts_before_guides`

#### CreateChallengeCommand.java
- **Updated Fields:**
  - Added `guides`: List<UUID>
  - Added `maxAttemptsBeforeGuides`: Integer

### 2. Application Layer Updates

#### ChallengeCommandService.java (Interface)
- **New Methods:**
  - `void handle(AddGuideCommand command)`
  - `void handle(RemoveGuideCommand command)`

#### ChallengeCommandServiceImpl.java
- **Implementations:**
  - `handle(AddGuideCommand)`: Adds a guide to a challenge
  - `handle(RemoveGuideCommand)`: Removes a guide from a challenge

#### New Command Classes
- **AddGuideCommand.java**: Record for adding a guide to a challenge
- **RemoveGuideCommand.java**: Record for removing a guide from a challenge

### 3. Interface Layer Updates

#### ChallengeController.java
- **New Endpoints:**
  - `POST /api/v1/challenges/{challengeId}/guides/{guideId}`: Add a guide to a challenge
  - `DELETE /api/v1/challenges/{challengeId}/guides/{guideId}`: Remove a guide from a challenge

- **Security:**
  - Both endpoints require TEACHER or ADMIN role
  - Ownership validation: Only the challenge owner can add/remove guides

#### CreateChallengeResource.java
- **Updated Fields:**
  - Added `guides`: List<UUID>
  - Added `maxAttemptsBeforeGuides`: Integer

#### ChallengeResource.java
- **Updated Fields:**
  - Added `guides`: List<UUID>
  - Added `maxAttemptsBeforeGuides`: Integer

#### CreateChallengeCommandFromResourceAssembler.java
- Updated to include guides and maxAttemptsBeforeGuides in command creation

#### ChallengeResourceFromEntityAssembler.java
- Updated to include guides and maxAttemptsBeforeGuides in response

### 4. Infrastructure Updates

#### JwtUtil.java
- **New Method:**
  - `generateToken(String userId, List<String> roles)`: Generate JWT tokens for testing purposes
  - Includes userId claim, roles claim, issued at, and expiration (1 hour)

### 5. Testing

#### ChallengeGuidesIntegrationTest.java
Comprehensive integration tests covering:

**Basic Functionality:**
1. `shouldCreateChallengeWithGuidesAndMaxAttempts`: Create challenge with guides and max attempts
2. `shouldCreateChallengeWithoutGuides`: Create challenge without guides (optional fields)
3. `shouldAddGuideToChallenge`: Add a guide to existing challenge
4. `shouldRemoveGuideFromChallenge`: Remove a guide from challenge
5. `shouldGetChallengeWithGuides`: Retrieve challenge with guides included

**Edge Cases:**
6. `shouldNotAddDuplicateGuide`: Prevent duplicate guides
7. `shouldAddMultipleGuidesSequentially`: Add multiple guides one by one
8. `shouldRemoveSpecificGuideWhileKeepingOthers`: Remove one guide while preserving others
9. `shouldCreateChallengeWithMaxAttemptsButNoGuides`: Max attempts without guides
10. `shouldHandleInvalidGuideIdFormat`: Invalid UUID format handling

**Security:**
11. `shouldReturn403WhenNonOwnerTriesToAddGuide`: Non-owner cannot add guides
12. `shouldReturn403WhenNonOwnerTriesToRemoveGuide`: Non-owner cannot remove guides
13. `shouldReturn403WhenStudentTriesToAddGuide`: Students cannot add guides
14. `shouldReturn403WhenStudentTriesToRemoveGuide`: Students cannot remove guides

**Error Handling:**
15. `shouldReturn404WhenAddingGuideToNonExistentChallenge`: 404 for non-existent challenge

## API Documentation

### Create Challenge with Guides
```http
POST /api/v1/challenges
Authorization: Bearer {teacher_token}
Content-Type: application/json

{
  "name": "Challenge Name",
  "description": "Challenge Description",
  "experiencePoints": 150,
  "difficulty": "MEDIUM",
  "tags": ["#tag1", "#tag2"],
  "guides": ["550e8400-e29b-41d4-a716-446655440000"],
  "maxAttemptsBeforeGuides": 5
}
```

### Add Guide to Challenge
```http
POST /api/v1/challenges/{challengeId}/guides/{guideId}
Authorization: Bearer {teacher_token}
```

### Remove Guide from Challenge
```http
DELETE /api/v1/challenges/{challengeId}/guides/{guideId}
Authorization: Bearer {teacher_token}
```

### Get Challenge (includes guides)
```http
GET /api/v1/challenges/{challengeId}
Authorization: Bearer {token}
```

Response:
```json
{
  "id": "...",
  "teacherId": "...",
  "name": "Challenge Name",
  "description": "...",
  "experiencePoints": 150,
  "difficulty": "MEDIUM",
  "status": "PUBLISHED",
  "tags": ["#tag1", "#tag2"],
  "stars": [],
  "guides": ["550e8400-e29b-41d4-a716-446655440000"],
  "maxAttemptsBeforeGuides": 5
}
```

## Business Logic

### Guide Management Rules
1. **Guides are optional**: Challenges can be created without guides
2. **No duplicates**: The same guide cannot be added twice to a challenge
3. **Owner-only operations**: Only the challenge owner (teacher) can add/remove guides
4. **Persistence**: Guides are stored in a separate table (`challenge_guides`)
5. **Max attempts**: Teachers can specify when guides should be shown to struggling students

### Use Case Scenario
1. Teacher creates a challenge with difficulty level HARD
2. Teacher adds 2-3 learning guides to help students
3. Teacher sets `maxAttemptsBeforeGuides = 3`
4. When a student fails 3 times, the system can show the available guides
5. Student accesses guides to learn and improve their solution

## Database Schema Changes

### New Table: challenge_guides
```sql
CREATE TABLE challenge_guides (
    challenge_id UUID NOT NULL REFERENCES challenges(id),
    guide_id UUID NOT NULL,
    PRIMARY KEY (challenge_id, guide_id)
);
```

### Modified Table: challenges
```sql
ALTER TABLE challenges 
ADD COLUMN max_attempts_before_guides INTEGER;
```

## Configuration Requirements

### Environment Variables
- `JWT_SECRET`: Required for JWT token generation/validation (minimum 256 bits for HMAC512)

### Test Configuration
Tests use `@TestPropertySource` to provide JWT secret:
```java
@TestPropertySource(properties = {
    "jwt.secret=test-secret-key-for-testing-purposes-minimum-256-bits-required-here-for-hmac512"
})
```

## Best Practices Followed

1. **Domain-Driven Design**: Business logic encapsulated in Challenge aggregate
2. **Command Pattern**: Separate commands for each operation (AddGuide, RemoveGuide)
3. **Security**: Role-based access control and ownership validation
4. **RESTful API**: Proper HTTP methods and status codes
5. **Comprehensive Testing**: 15 integration tests covering all scenarios
6. **Validation**: Prevents duplicate guides, validates ownership
7. **Separation of Concerns**: Clear boundaries between layers (domain, application, interface)
8. **Idempotency**: Adding the same guide twice has no side effects

## Future Enhancements

1. **Guide Ordering**: Allow teachers to set the order in which guides are presented
2. **Guide Tracking**: Track which guides students have viewed
3. **Conditional Guides**: Show different guides based on the type of error
4. **Guide Analytics**: Track guide effectiveness (did it help students pass?)
5. **Bulk Operations**: Add/remove multiple guides at once
6. **Guide Validation**: Validate that guide IDs exist in a Guide microservice

## Notes

- Guides are referenced by UUID only - the actual guide content is managed by a separate microservice
- The `maxAttemptsBeforeGuides` field is nullable - if not set, guides won't be shown automatically
- The implementation follows the existing patterns in the codebase (commands, assemblers, etc.)
- All changes are backward compatible - existing challenges without guides continue to work

