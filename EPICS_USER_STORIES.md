# Epics and User Stories

## Epic 1: Challenge Management
**Description**: Teachers need to create, configure, and publish programming challenges for students

### User Story 1.1: Create Challenge
**As a Teacher**, I want to create a new programming challenge with basic information, so that students can work on coding problems.

**Acceptance Criteria:**
```
Scenario: Successfully create a challenge
  Given I am a logged-in teacher
  When I submit challenge details with name "Hello World", description "Basic greeting program", and 100 experience points
  Then a new challenge should be created with DRAFT status
  And I should receive the challenge ID
  And the challenge should be associated with my teacher ID

Scenario: Validation fails for missing required fields
  Given I am a logged-in teacher
  When I submit challenge details without a name
  Then I should receive a validation error
  And no challenge should be created
```
**Story Points**: 3

### User Story 1.2: Add Code Version to Challenge
**As a Teacher**, I want to add language-specific versions to my challenges, so that students can solve the same problem in different programming languages.

**Acceptance Criteria:**
```
Scenario: Successfully add Java version to challenge
  Given I have a challenge in DRAFT status
  When I add a Java code version with initial code template
  Then the challenge should have a new CodeVersion with JAVA language
  And the CodeVersion should be linked to the challenge
  And the initial code should be set

Scenario: Add multiple language versions
  Given I have a challenge with a Java version
  When I add Python and JavaScript versions
  Then the challenge should have three CodeVersions
  And each version should have appropriate language settings
```
**Story Points**: 5

### User Story 1.3: Configure Test Cases
**As a Teacher**, I want to define test cases for each code version, so that student solutions can be automatically validated.

**Acceptance Criteria:**
```
Scenario: Add test case with input and expected output
  Given I have a CodeVersion for a challenge
  When I add a test case with input "5" and expected output "25"
  Then the CodeVersionTest should be created
  And it should be linked to the CodeVersion
  And the test should have input, expected output, and failure message

Scenario: Add test with custom validation code
  Given I have a CodeVersion for a challenge
  When I add a test case with custom validation logic
  Then the test should store the custom validation code
  And it should be used during solution execution
```
**Story Points**: 8

### User Story 1.4: Publish Challenge
**As a Teacher**, I want to publish my completed challenges, so that students can start working on them.

**Acceptance Criteria:**
```
Scenario: Successfully publish challenge
  Given I have a challenge in DRAFT status with at least one CodeVersion
  When I publish the challenge
  Then the challenge status should change to PUBLISHED
  And students should be able to see it in published challenges list

Scenario: Cannot publish without code versions
  Given I have a challenge in DRAFT status without any CodeVersions
  When I try to publish the challenge
  Then I should receive an error
  And the status should remain DRAFT
```
**Story Points**: 3

### User Story 1.5: Update Challenge Details
**As a Teacher**, I want to modify my challenge information, so that I can improve or correct challenge content.

**Acceptance Criteria:**
```
Scenario: Update challenge name and description
  Given I have a challenge in DRAFT status
  When I update the name to "Updated Challenge" and description
  Then the challenge should reflect the new information
  And the update timestamp should be recorded

Scenario: Update experience points
  Given I have a challenge
  When I change the experience points from 100 to 150
  Then the challenge should have the new experience points value
```
**Story Points**: 3

## Epic 2: Solution Submission
**Description**: Students need to start challenges, develop solutions, and submit them for evaluation

### User Story 2.1: Start Challenge
**As a Student**, I want to start working on a published challenge, so that I can begin developing my solution.

**Acceptance Criteria:**
```
Scenario: Successfully start a challenge
  Given I am a logged-in student
  And there is a published challenge with Java version
  When I start the challenge selecting Java language
  Then a ChallengeStartedEvent should be triggered
  And a new Solution should be created for me
  And the Solution should have NO_TESTED status

Scenario: Cannot start unpublished challenge
  Given there is a challenge in DRAFT status
  When I try to start the challenge
  Then I should receive an error
  And no Solution should be created
```
**Story Points**: 5

### User Story 2.2: Update Solution Code
**As a Student**, I want to save my progress while developing the solution, so that I don't lose my work.

**Acceptance Criteria:**
```
Scenario: Update solution code
  Given I have a solution with existing code
  When I update the code with new implementation
  Then the Solution should store the new code
  And the last update timestamp should be recorded
  And the status should remain NO_TESTED

Scenario: Update with invalid code
  Given I have a solution
  When I submit empty code
  Then I should receive a validation error
  And the original code should be preserved
```
**Story Points**: 3

### User Story 2.3: Submit Solution for Testing
**As a Student**, I want to submit my solution for automatic evaluation, so that I can see if it passes all test cases.

**Acceptance Criteria:**
```
Scenario: Successfully submit solution
  Given I have a solution with code ready for testing
  When I submit the solution
  Then a SubmitSolutionCommand should be processed
  And the solution attempts should be incremented
  And the CodeExecutionGrpcService should be called
  And a SolutionReport should be generated

Scenario: Solution passes all tests
  Given I submitted a correct solution
  When the code execution completes successfully
  Then the Solution status should change to SUCCESS
  And the SolutionReport should list all successful tests

Scenario: Solution fails some tests
  Given I submitted an incorrect solution
  When the code execution finds failures
  Then the Solution status should change to FAILED
  And the SolutionReport should show which tests failed
```
**Story Points**: 13

### User Story 2.4: View Solution History
**As a Student**, I want to see all my previous submissions and their results, so that I can track my progress.

**Acceptance Criteria:**
```
Scenario: View solution reports
  Given I have submitted multiple solutions
  When I request my solution reports
  Then I should see all reports for my StudentId
  And each report should show time taken and memory used
  And reports should be ordered by submission date

Scenario: View specific solution details
  Given I have a solution
  When I request the solution by ID
  Then I should see the current code and status
  And I should see the number of attempts
```
**Story Points**: 5

## Epic 3: Code Execution & Reporting
**Description**: The system needs to execute student code and generate detailed reports

### User Story 3.1: Execute Code via gRPC
**As a Developer**, I want the system to communicate with CodeRunner service via gRPC, so that student code can be executed in an isolated environment.

**Acceptance Criteria:**
```
Scenario: Successful gRPC code execution
  Given a solution is submitted with Java code
  When the CodeExecutionGrpcService is called
  Then it should send code, language, and test cases to CodeRunner
  And receive execution results with success/failure status
  And return performance metrics (time, memory)

Scenario: Handle execution timeout
  Given a solution with infinite loop code
  When the CodeRunner times out
  Then the execution should fail gracefully
  And a timeout error should be recorded in the report
```
**Story Points**: 8

### User Story 3.2: Generate Solution Report
**As a Developer**, I want detailed execution reports to be created automatically, so that students can understand their performance.

**Acceptance Criteria:**
```
Scenario: Create comprehensive report
  Given a solution execution completed
  When CreateSolutionReportCommand is processed
  Then a SolutionReport should be created with:
  | Field | Value |
  |-------|-------|
  | solutionId | from the executed solution |
  | studentId | from the solution |
  | successfulTests | list of passed test IDs |
  | timeTaken | execution time in seconds |
  | memoryUsed | memory consumption in MB |

Scenario: Report with partial success
  Given some tests passed and some failed
  When the report is generated
  Then successfulTests should contain only passed test IDs
  And the solution status should be FAILED
```
**Story Points**: 5

### User Story 3.3: ACL Data Retrieval
**As a Developer**, I want the ACL to efficiently retrieve challenge data, so that solutions can access test cases without tight coupling.

**Acceptance Criteria:**
```
Scenario: Retrieve code version with tests
  Given a solution needs test cases for execution
  When ExternalChallengesService.getCodeVersion() is called
  Then it should return CodeVersion with language and initial code
  And include all associated CodeVersionTests
  And not expose audit fields or internal data

Scenario: Get challenge metadata
  Given a solution needs challenge information
  When ExternalChallengesService.getChallenge() is called
  Then it should return challenge name and description
  And exclude sensitive teacher information
```
**Story Points**: 5

## Epic 4: Platform Administration
**Description**: Developers need tools to manage and monitor the platform

### User Story 4.1: Query Published Challenges
**As a Developer**, I want to retrieve all published challenges efficiently, so that the platform can display available content.

**Acceptance Criteria:**
```
Scenario: Get all published challenges
  Given there are multiple challenges with different statuses
  When GetAllPublishedChallengesQuery is executed
  Then only challenges with PUBLISHED status should be returned
  And results should be ordered by creation date
  And include basic challenge information

Scenario: Filter challenges by teacher
  Given a teacher has multiple challenges
  When GetChallengesByTeacherIdQuery is executed
  Then only challenges created by that teacher should be returned
```
**Story Points**: 3

### User Story 4.2: Manage Solution Reports
**As a Developer**, I want to clean up old solution reports, so that the database doesn't grow indefinitely.

**Acceptance Criteria:**
```
Scenario: Delete reports by student
  Given a student has multiple solution reports
  When DeleteSolutionReportsByStudentIdCommand is executed
  Then all reports for that student should be removed
  And the operation should be logged

Scenario: Delete reports by solution
  Given a solution has multiple execution reports
  When DeleteSolutionReportBySolutionIdCommand is executed
  Then all reports for that solution should be removed
```
**Story Points**: 3

### User Story 4.3: Challenge Version Management
**As a Developer**, I want to update code versions and tests, so that challenges can be improved over time.

**Acceptance Criteria:**
```
Scenario: Update code version initial code
  Given a CodeVersion exists
  When UpdateCodeVersionCommand is executed with new code
  Then the initial code should be updated
  And the modification timestamp should be recorded

Scenario: Update test case details
  Given a CodeVersionTest exists
  When UpdateCodeVersionTestCommand is executed
  Then the test input, expected output, and validation code should be updated
```
**Story Points**: 5