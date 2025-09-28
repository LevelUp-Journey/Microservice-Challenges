# Core Business User Stories

## Primary User Stories

### Epic 1.4: Publish Challenge
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

### Epic 2.1: Start Challenge
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

### Epic 2.3: Submit Solution for Testing
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

### Epic 3.1: Execute Code via gRPC
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

### Epic 3.2: Generate Solution Report
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

## Business Flow Summary

These 5 user stories represent the **critical path** of the platform:

1. **Epic 1.4: Publish Challenge** → Makes content available to students
2. **Epic 2.1: Start Challenge** → Initiates the learning process
3. **Epic 2.3: Submit Solution** → Core business transaction (evaluation request)
4. **Epic 3.1: Execute Code** → Technical validation of solutions
5. **Epic 3.2: Generate Report** → Provides feedback and closes the learning cycle

**Total Story Points**: 34
**Business Impact**: High - These stories enable the complete challenge-solution-evaluation cycle</content>
<parameter name="filePath">/Users/nanakusa/Desktop/LevelUpJourney/Microservice-Challenges/CORE_USER_STORIES.md