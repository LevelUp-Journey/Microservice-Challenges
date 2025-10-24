# Solutions API - Swagger Documentation and Automatic Code Initialization

## Overview
This document describes the enhancements made to the Solutions API, including complete Swagger/OpenAPI documentation and automatic code initialization when creating solutions.

## Date
October 23, 2025

## Changes Implemented

### 1. Enhanced Create Solution Endpoint
**Purpose**: Create a solution for a challenge with automatic initialization of code from the code version's default code.

**Endpoint**: `POST /api/v1/challenges/{challengeId}/code-versions/{codeVersionId}/solutions`

**Description**: 
- Creates a new solution automatically initialized with the code version's default/initial code
- Prevents duplicate solutions (one solution per student per code version)
- Validates that challenge and code version exist
- Extracts student ID from JWT token
- **No request body required** - code is automatically retrieved from code version

**Request Parameters**:
- `challengeId` (path) - UUID of the challenge
- `codeVersionId` (path) - UUID of the code version
- `Authorization` (header) - JWT bearer token

**Request Body**: None (removed for RESTful compliance - code is automatically initialized)

**Response Codes**:
- `201 Created` - Solution created successfully with default code
- `409 Conflict` - Student already has a solution for this code version
- `404 Not Found` - Challenge or code version not found
- `400 Bad Request` - Invalid challenge or code version ID format

**Business Rules**:
- A student can only have ONE solution per code version
- If a solution already exists, the endpoint returns 409 Conflict with a message to use PUT for updates
- Default code is retrieved from `CodeVersion.initialCode`
- If initialCode is null, an empty string is used

**Example Success Response** (201):
```json
{
  "solutionId": "123e4567-e89b-12d3-a456-426614174000",
  "challengeId": "223e4567-e89b-12d3-a456-426614174000",
  "codeVersionId": "323e4567-e89b-12d3-a456-426614174000",
  "studentId": "423e4567-e89b-12d3-a456-426614174000",
  "code": "// Default starter code\nfunction solve() {\n  return 0;\n}",
  "status": "IN_PROGRESS",
  "createdAt": "2025-10-23T10:30:00Z"
}
```

**Example Error Response** (409):
```json
{
  "message": "You already have a solution for this challenge code version. Use PUT to update it."
}
```

### 2. Enhanced Existing Endpoints with Swagger Documentation

All existing solutions endpoints now include comprehensive Swagger/OpenAPI annotations:

#### Get Solution by ID
- **Endpoint**: `GET /api/v1/solutions/{solutionId}`
- **Description**: Retrieve a specific solution by its unique identifier
- **Status Codes**: 200, 404, 400

#### Get Student's Solution
- **Endpoint**: `GET /api/v1/challenges/{challengeId}/code-versions/{codeVersionId}/solutions`
- **Description**: Retrieve the authenticated student's solution for a specific code version
- **Authentication**: Uses JWT token to identify student
- **Status Codes**: 200, 404, 400

#### Get Specific Student's Solution (Teachers/Admins)
- **Endpoint**: `GET /api/v1/students/{studentId}/code-versions/{codeVersionId}/solutions`
- **Description**: Retrieve a solution for a specific student and code version
- **Authorization**: Intended for teachers/admins
- **Status Codes**: 200, 404, 400

#### Update Solution
- **Endpoint**: `PUT /api/v1/solutions/{solutionId}`
- **Description**: Update the code of an existing solution
- **Status Codes**: 200, 404, 400, 500

#### Submit Solution for Evaluation
- **Endpoint**: `PUT /api/v1/solutions/{solutionId}/submissions`
- **Description**: Submit a solution to be evaluated by the code runner service
- **Status Codes**: 200, 404, 400, 500

### 3. Error Handling Enhancements

Created `ErrorResponse` resource for consistent error messages:

**Location**: `com.levelupjourney.microservicechallenges.solutions.interfaces.rest.resources.ErrorResponse`

```java
public record ErrorResponse(String message) {}
```

**Error Scenarios Handled**:
- Invalid UUID format (400 Bad Request)
- Resource not found (404 Not Found)
- Duplicate resources (409 Conflict)
- Internal server errors (500 Internal Server Error)

**Example Error Responses**:
```json
{
  "message": "Invalid ID format: For input string: 'invalid-uuid'"
}
```

```json
{
  "message": "Solution not found with id: 123e4567-e89b-12d3-a456-426614174000"
}
```

### 4. Validation Rules

**Duplicate Solution Prevention**:
- Query: `GetSolutionByChallengeIdAndCodeVersionIdAndStudentIdQuery`
- Check performed BEFORE creating solution
- Returns 409 Conflict if solution exists
- Applies to both "start challenge" and "create solution" endpoints

**Code Version Validation**:
- Query: `GetCodeVersionByIdQuery`
- Validates code version exists before creating solution
- Returns 404 Not Found if code version doesn't exist

### 5. Dependencies Added

**CodeVersionQueryService**:
- Purpose: Retrieve code versions to access initial/default code
- Injection: Added to SolutionController constructor
- Usage: Fetching `initialCode` when starting challenges

**Swagger Annotations**:
- `@Operation` - Describes endpoint purpose
- `@ApiResponse` - Documents response codes
- `@ApiResponses` - Groups multiple responses
- `@Tag` - Groups related endpoints

## User Experience Flow

### Creating a Solution (RESTful Flow)

1. **Student navigates to challenge**
2. **Student clicks "Start Challenge" or "Solve Challenge"**
3. **Frontend calls**: `POST /api/v1/challenges/{challengeId}/code-versions/{codeVersionId}/solutions`
   - No request body needed
   - Only JWT token in Authorization header
4. **Backend**:
   - Extracts student ID from JWT
   - Checks if solution already exists
   - If exists → Returns 409 Conflict
   - If not exists:
     - Retrieves code version
     - Gets initial code from code version
     - Creates solution with initial code
     - Returns 201 Created with solution
5. **Student sees code editor with default code loaded**

### Working on Solution

1. **Student modifies code in editor**
2. **Student clicks "Save"**
3. **Frontend calls**: `PUT /api/v1/solutions/{solutionId}` with updated code
4. **Backend updates solution**
5. **Student clicks "Submit"**
6. **Frontend calls**: `PUT /api/v1/solutions/{solutionId}/submissions`
7. **Backend evaluates code and returns results**

## API Documentation Access

When running the application, access Swagger UI at:
- **URL**: `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON**: `http://localhost:8080/v3/api-docs`

All solutions endpoints are grouped under the **"Solutions"** tag with the description:
> "Endpoints for managing student solutions and challenge attempts"

## Technical Implementation Details

### Code Version Initial Code
The `CodeVersion` entity has an `initialCode` field that contains starter code for challenges:
- Can be simple comments or boilerplate
- Can be partially implemented functions
- Can be empty (defaults to empty string in endpoint)

### Query Services Used
1. `SolutionQueryService` - Check existing solutions
2. `CodeVersionQueryService` - Retrieve code version details

### Command Services Used
1. `SolutionCommandService` - Create and update solutions

### Value Objects
- `ChallengeId`
- `CodeVersionId`
- `SolutionId`
- `StudentId`

All IDs are UUIDs wrapped in value objects following DDD principles.

## Testing Recommendations

### Test Cases for "Create Solution" Endpoint

1. **Happy Path**:
   - Valid challengeId and codeVersionId
   - Student has no existing solution
   - No request body needed
   - Should return 201 with solution containing initial code

2. **Duplicate Prevention**:
   - Student already has solution for code version
   - Should return 409 Conflict

3. **Code Version Not Found**:
   - Invalid or non-existent codeVersionId
   - Should return 404 Not Found

4. **Invalid UUID Format**:
   - Malformed challengeId or codeVersionId
   - Should return 400 Bad Request

5. **Missing Authorization**:
   - No JWT token provided
   - Should return 401 Unauthorized (handled by security layer)

### Test Cases for Error Handling

1. Test all endpoints return proper error responses
2. Verify error messages are descriptive
3. Confirm HTTP status codes match documentation
4. Validate that duplicate prevention works consistently

## Security Considerations

1. **JWT Authentication**: All endpoints require valid JWT token
2. **Student Isolation**: Students can only access their own solutions
3. **Teacher/Admin Access**: Special endpoint for viewing any student's solution
4. **Authorization Header**: Required for all authenticated endpoints

## Future Enhancements

1. **Rate Limiting**: Prevent abuse of submission endpoint
2. **Solution Templates**: Support for multiple initial code templates per language
3. **Partial Submissions**: Allow saving work in progress without evaluation
4. **Solution History**: Track all code changes over time
5. **Collaboration**: Allow pair programming or solution sharing

## Related Documentation

- `API_Endpoints_Documentation.md` - Complete API reference
- `SWAGGER_DOCUMENTATION_SUMMARY.md` - General Swagger implementation
- `challenge-update-examples.http` - HTTP request examples
- `complete-flow-endpoints.http` - Full workflow examples
