# Ubiquitous Language - Concept Summary

## Core Domain Concepts

**Challenge**: Core aggregate representing a programming challenge that teachers create for students to solve, containing description, experience points, and code versions

**Solution**: Student's submission for a specific challenge in a specific programming language, tracking code attempts and progress

**CodeVersion**: Language-specific version of a challenge with initial code template and associated tests

**SolutionReport**: Detailed execution report generated when a solution is tested, storing performance metrics and results

**CodeVersionTest**: Individual test case that validates a solution for a specific code version

## Key Value Objects

**ChallengeId**: Strongly-typed identifier for Challenge aggregates

**SolutionId**: Strongly-typed identifier for Solution aggregates

**StudentId**: References student users across bounded contexts

**CodeVersionId**: Strongly-typed identifier for CodeVersion aggregates

**TeacherId**: References teacher users who create challenges

## Domain Status Enumerations

**ChallengeStatus**: Represents lifecycle state of a challenge (DRAFT, PUBLISHED, HIDDEN)

**SolutionStatus**: Represents current state of a solution (SUCCESS, FAILED, NO_TESTED, MAX_ATTEMPTS_REACHED)

**CodeLanguage**: Supported programming languages for challenge solutions (JAVA, C_PLUS_PLUS, JAVASCRIPT, PYTHON)

**UserRole**: Defines user roles across the system (STUDENT, TEACHER, ADMIN)

## Primary Commands

**CreateChallengeCommand**: Creates a new challenge

**SubmitSolutionCommand**: Submits solution for testing and evaluation

**CreateSolutionReportCommand**: Creates a new execution report

**StartChallengeCommand**: Initiates student work on a challenge

**PublishChallengeCommand**: Changes challenge status to PUBLISHED

## Key Queries

**GetSolutionByIdQuery**: Retrieves a specific solution

**GetChallengeByIdQuery**: Retrieves a specific challenge

**GetReportsByStudentIdQuery**: Retrieves all reports for a student

**GetAllPublishedChallengesQuery**: Retrieves all published challenges

## Domain Services

**SolutionCommandService**: Handles write operations for Solution aggregates

**ChallengeCommandService**: Handles write operations for Challenge aggregates

**SolutionReportCommandService**: Handles write operations for SolutionReport aggregates

**ExternalChallengesService**: Provides external access to Challenges context from Solutions context

## Domain Events

**ChallengeStartedEvent**: Domain event triggered when a student begins working on a challenge

## Business Processes

**Solution Submission Flow**: Process from challenge start through code execution to report generation

**Challenge Creation Flow**: Process from challenge creation through publication to student access

**Challenge Resolution Process**: Logic for success/failure paths and retry attempts

## Architectural Patterns

**CQRS**: Command Query Responsibility Segregation pattern separating read and write operations

**Anti-Corruption Layer**: Protects domain integrity when integrating with external contexts

**Repository Pattern**: Abstracts data access layer from domain logic

**Domain Events**: Decouple aggregates and enable eventual consistency

## Shared Infrastructure

**AuditableAbstractAggregateRoot**: Base class providing consistent auditing capabilities across aggregates

**ChallengeTag**: Categorization label for organizing challenges by topic or difficulty