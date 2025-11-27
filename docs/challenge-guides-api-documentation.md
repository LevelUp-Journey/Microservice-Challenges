# Challenge Guides API Endpoints

## Overview
This document describes the new API endpoints for managing learning guides in challenges.

## Endpoints

### 1. Create Challenge with Guides

Creates a new challenge with optional guides and max attempts configuration.

**Endpoint:** `POST /api/v1/challenges`

**Authorization:** Bearer token (TEACHER or ADMIN role required)

**Request Body:**
```json
{
  "name": "Learn Loops in Java",
  "description": "Master for loops, while loops, and do-while loops",
  "experiencePoints": 150,
  "difficulty": "MEDIUM",
  "tags": ["#java", "#loops", "#beginner"],
  "guides": [
    "550e8400-e29b-41d4-a716-446655440000",
    "6ba7b810-9dad-11d1-80b4-00c04fd430c8"
  ],
  "maxAttemptsBeforeGuides": 5
}
```

**Request Fields:**
- `name` (string, required): Name of the challenge
- `description` (string, required): Detailed description
- `experiencePoints` (integer, required): XP awarded upon completion
- `difficulty` (string, required): EASY, MEDIUM, or HARD
- `tags` (array of strings, optional): Tags for categorization
- `guides` (array of UUIDs, optional): IDs of associated learning guides
- `maxAttemptsBeforeGuides` (integer, optional): Number of attempts before guides are shown

**Response:** `201 Created`
```json
{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "teacherId": "789e4567-e89b-12d3-a456-426614174000",
  "name": "Learn Loops in Java",
  "description": "Master for loops, while loops, and do-while loops",
  "experiencePoints": 150,
  "difficulty": "MEDIUM",
  "status": "DRAFT",
  "tags": ["#java", "#loops", "#beginner"],
  "stars": [],
  "guides": [
    "550e8400-e29b-41d4-a716-446655440000",
    "6ba7b810-9dad-11d1-80b4-00c04fd430c8"
  ],
  "maxAttemptsBeforeGuides": 5
}
```

**Error Responses:**
- `400 Bad Request`: Invalid data (e.g., XP exceeds difficulty max score)
- `403 Forbidden`: Not authenticated or insufficient permissions

---

### 2. Add Guide to Challenge

Adds a learning guide to an existing challenge.

**Endpoint:** `POST /api/v1/challenges/{challengeId}/guides/{guideId}`

**Authorization:** Bearer token (TEACHER or ADMIN role required, must be challenge owner)

**Path Parameters:**
- `challengeId` (UUID): ID of the challenge
- `guideId` (UUID): ID of the guide to add

**Example Request:**
```http
POST /api/v1/challenges/123e4567-e89b-12d3-a456-426614174000/guides/550e8400-e29b-41d4-a716-446655440000
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...
```

**Response:** `200 OK`
```json
{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "teacherId": "789e4567-e89b-12d3-a456-426614174000",
  "name": "Learn Loops in Java",
  "description": "Master for loops, while loops, and do-while loops",
  "experiencePoints": 150,
  "difficulty": "MEDIUM",
  "status": "DRAFT",
  "tags": ["#java", "#loops", "#beginner"],
  "stars": [],
  "guides": [
    "550e8400-e29b-41d4-a716-446655440000"
  ],
  "maxAttemptsBeforeGuides": 5
}
```

**Error Responses:**
- `400 Bad Request`: Invalid UUID format
- `403 Forbidden`: Not the challenge owner
- `404 Not Found`: Challenge not found

**Notes:**
- Adding a duplicate guide has no effect (idempotent operation)
- Guides are returned in the order they were added

---

### 3. Remove Guide from Challenge

Removes a learning guide from a challenge.

**Endpoint:** `DELETE /api/v1/challenges/{challengeId}/guides/{guideId}`

**Authorization:** Bearer token (TEACHER or ADMIN role required, must be challenge owner)

**Path Parameters:**
- `challengeId` (UUID): ID of the challenge
- `guideId` (UUID): ID of the guide to remove

**Example Request:**
```http
DELETE /api/v1/challenges/123e4567-e89b-12d3-a456-426614174000/guides/550e8400-e29b-41d4-a716-446655440000
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...
```

**Response:** `200 OK`
```json
{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "teacherId": "789e4567-e89b-12d3-a456-426614174000",
  "name": "Learn Loops in Java",
  "description": "Master for loops, while loops, and do-while loops",
  "experiencePoints": 150,
  "difficulty": "MEDIUM",
  "status": "DRAFT",
  "tags": ["#java", "#loops", "#beginner"],
  "stars": [],
  "guides": [],
  "maxAttemptsBeforeGuides": 5
}
```

**Error Responses:**
- `400 Bad Request`: Invalid UUID format
- `403 Forbidden`: Not the challenge owner
- `404 Not Found`: Challenge not found

**Notes:**
- Removing a non-existent guide has no effect (idempotent operation)
- Other guides remain unaffected

---

### 4. Get Challenge (Updated)

Retrieves a challenge including its guides.

**Endpoint:** `GET /api/v1/challenges/{challengeId}`

**Authorization:** Bearer token required

**Path Parameters:**
- `challengeId` (UUID): ID of the challenge

**Example Request:**
```http
GET /api/v1/challenges/123e4567-e89b-12d3-a456-426614174000
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...
```

**Response:** `200 OK`
```json
{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "teacherId": "789e4567-e89b-12d3-a456-426614174000",
  "name": "Learn Loops in Java",
  "description": "Master for loops, while loops, and do-while loops",
  "experiencePoints": 150,
  "difficulty": "MEDIUM",
  "status": "PUBLISHED",
  "tags": ["#java", "#loops", "#beginner"],
  "stars": [
    {
      "userId": "user-123",
      "starredAt": "2025-11-08T22:30:00"
    }
  ],
  "guides": [
    "550e8400-e29b-41d4-a716-446655440000",
    "6ba7b810-9dad-11d1-80b4-00c04fd430c8"
  ],
  "maxAttemptsBeforeGuides": 5
}
```

**Error Responses:**
- `403 Forbidden`: Challenge is DRAFT/HIDDEN and user is not the owner
- `404 Not Found`: Challenge not found

**Notes:**
- PUBLISHED challenges are accessible to all authenticated users
- DRAFT/HIDDEN challenges are only accessible to their owners

---

## Usage Examples

### Example 1: Create Challenge with Guides
```bash
curl -X POST http://localhost:8083/api/v1/challenges \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Array Manipulation Basics",
    "description": "Learn to manipulate arrays efficiently",
    "experiencePoints": 100,
    "difficulty": "EASY",
    "tags": ["#arrays", "#basics"],
    "guides": ["guide-uuid-1", "guide-uuid-2"],
    "maxAttemptsBeforeGuides": 3
  }'
```

### Example 2: Add Additional Guide
```bash
curl -X POST http://localhost:8083/api/v1/challenges/CHALLENGE_ID/guides/GUIDE_ID \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### Example 3: Remove a Guide
```bash
curl -X DELETE http://localhost:8083/api/v1/challenges/CHALLENGE_ID/guides/GUIDE_ID \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

## Business Rules

1. **Guide Creation**: Guides can be added when creating a challenge or afterward
2. **Duplicate Prevention**: Adding the same guide twice has no effect
3. **Ownership**: Only the challenge owner can add/remove guides
4. **Optional Fields**: Both `guides` and `maxAttemptsBeforeGuides` are optional
5. **Guide Visibility**: Students see guides based on `maxAttemptsBeforeGuides` setting
6. **Persistence**: Guides are stored in a junction table for efficient querying

---

## Integration with Solution System

The `maxAttemptsBeforeGuides` field works in conjunction with the solution submission system:

1. Student submits a solution (attempt)
2. System tracks number of failed attempts
3. When attempts reach `maxAttemptsBeforeGuides`:
   - Student is notified that guides are available
   - UI displays links to the associated guides
   - Student can access guides to improve their solution

**Future Enhancement:** The solution system will automatically check and notify students when guides become available.

---

## Testing

### Test Data Setup
```javascript
// Create a challenge with guides
const challengeWithGuides = {
  name: "Test Challenge",
  description: "For testing",
  experiencePoints: 100,
  difficulty: "EASY",
  tags: ["#test"],
  guides: [
    "550e8400-e29b-41d4-a716-446655440000",
    "6ba7b810-9dad-11d1-80b4-00c04fd430c8"
  ],
  maxAttemptsBeforeGuides: 3
};
```

### Test Scenarios Covered
- ✅ Create challenge with guides
- ✅ Create challenge without guides
- ✅ Add guide to existing challenge
- ✅ Remove guide from challenge
- ✅ Prevent duplicate guides
- ✅ Validate ownership on guide operations
- ✅ Handle invalid UUID formats
- ✅ Return 404 for non-existent challenges
- ✅ Enforce role-based access control

---

## Security Considerations

1. **Authentication Required**: All endpoints require valid JWT token
2. **Role-Based Access**: Only TEACHER and ADMIN roles can manage guides
3. **Ownership Validation**: Users can only modify their own challenges
4. **Input Validation**: UUIDs are validated for correct format
5. **No Guide Content Exposure**: Only guide IDs are stored/returned

---

## Performance Notes

- Guide IDs are stored in a separate table (`challenge_guides`)
- Queries join this table when retrieving challenge details
- Index on `challenge_id` for fast lookup
- Composite primary key prevents duplicate entries at database level

---

## Migration Path

For existing challenges without guides:
1. `guides` field returns empty array `[]`
2. `maxAttemptsBeforeGuides` returns `null`
3. Teachers can add guides at any time using the add guide endpoint
4. No migration script needed - fields are nullable

