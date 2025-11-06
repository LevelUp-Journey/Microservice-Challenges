package com.levelupjourney.microservicechallenges.solutions.application.internal.commandservices;

import com.levelupjourney.microservicechallenges.challenges.domain.services.TimeBasedScoringStrategy;
import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.ScoringResult;
import com.levelupjourney.microservicechallenges.shared.infrastructure.messaging.kafka.KafkaProducerService;
import com.levelupjourney.microservicechallenges.solutions.application.internal.outboundservices.acl.ExternalChallengesService;
import com.levelupjourney.microservicechallenges.solutions.application.internal.outboundservices.grpc.CodeRunnerExecutionService;
import com.levelupjourney.microservicechallenges.solutions.domain.model.aggregates.Solution;
import com.levelupjourney.microservicechallenges.solutions.domain.model.commands.CreateSolutionCommand;
import com.levelupjourney.microservicechallenges.solutions.domain.model.commands.SubmitSolutionCommand;
import com.levelupjourney.microservicechallenges.solutions.domain.model.commands.UpdateSolutionCommand;
import com.levelupjourney.microservicechallenges.solutions.domain.model.events.ChallengeCompletedEvent;
import com.levelupjourney.microservicechallenges.solutions.domain.model.queries.GetSolutionByIdQuery;
import com.levelupjourney.microservicechallenges.solutions.domain.model.valueobjects.SolutionReportId;
import com.levelupjourney.microservicechallenges.solutions.domain.model.valueobjects.SubmissionResult;
import com.levelupjourney.microservicechallenges.solutions.domain.services.SolutionCommandService;
import com.levelupjourney.microservicechallenges.solutions.domain.services.SolutionQueryService;
import com.levelupjourney.microservicechallenges.solutions.infrastructure.persistence.jpa.repositories.SolutionRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class SolutionCommandServiceImpl implements SolutionCommandService {
    private final ExternalChallengesService externalChallengesService;
    private final SolutionQueryService solutionQueryService;
    private final SolutionRepository solutionRepository;
    private final CodeRunnerExecutionService codeRunnerExecutionService;
    private final KafkaProducerService kafkaProducerService;
    private final TimeBasedScoringStrategy timeBasedScoringStrategy;

    public SolutionCommandServiceImpl(ExternalChallengesService externalChallengesService,
                                    SolutionQueryService solutionQueryService,
                                    SolutionRepository solutionRepository,
                                    CodeRunnerExecutionService codeRunnerExecutionService,
                                    KafkaProducerService kafkaProducerService,
                                    TimeBasedScoringStrategy timeBasedScoringStrategy) {
        this.externalChallengesService = externalChallengesService;
        this.solutionQueryService = solutionQueryService;
        this.solutionRepository = solutionRepository;
        this.codeRunnerExecutionService = codeRunnerExecutionService;
        this.kafkaProducerService = kafkaProducerService;
        this.timeBasedScoringStrategy = timeBasedScoringStrategy;
    }

    @Override
    public Optional<Solution> handle(CreateSolutionCommand command) {
        var solution = new Solution(command);
        var savedSolution = solutionRepository.save(solution);
        return Optional.of(savedSolution);
    }

    @Override
    public SubmissionResult handle(SubmitSolutionCommand command) {
        // 📝 LOG INICIAL DE LA SUBMISSION
        log.info("🎯 =============== SUBMIT SOLUTION PROCESS STARTED ===============");
        log.info("📋 Submit Solution Command received:");
        log.info("  - Solution ID: '{}'", command.solutionId().id());
        log.info("  - Student ID: '{}'", command.studentId().id());
        log.info("  - Code length: {} characters", command.code() != null ? command.code().length() : 0);
        
        if (command.code() != null && !command.code().trim().isEmpty()) {
            String codePreview = command.code().length() > 200 ? command.code().substring(0, 200) + "..." : command.code();
            log.info("  - Code preview:\n{}", codePreview);
        }
        
        // 1. Verify solution exists and get its CodeVersion
        log.info("🔍 Step 1: Validating solution exists...");
        var solution = solutionQueryService.handle(
                new GetSolutionByIdQuery(command.solutionId())
        );

        if (solution.isEmpty()) {
            log.error("❌ Solution not found: {}", command.solutionId().id());
            return SubmissionResult.failure("Solution not found: " + command.solutionId().id());
        }

        var existingSolution = solution.get();
        log.info("✅ Solution found:");
        log.info("  - Code Version ID: '{}'", existingSolution.getCodeVersionId().id());
        log.info("  - Current status: '{}'", existingSolution.getStatus());

        // Record submission attempt to update lastAttemptAt timestamp
        existingSolution.recordSubmissionAttempt();
        solutionRepository.save(existingSolution);
        log.info("✅ Submission attempt recorded");

        try {
            // 2. Get code version details (language + tests) through ACL
            log.info("🔍 Step 2: Fetching CodeVersion details from external service...");
            var codeVersionDetails = externalChallengesService.getCodeVersionDetailsForSubmission(
                existingSolution.getCodeVersionId().id().toString()
            );

            log.info("✅ CodeVersion details retrieved:");
            log.info("  - Programming Language: '{}'", codeVersionDetails.codeLanguage());
            log.info("  - Total Tests: {}", codeVersionDetails.tests().size());
            log.info("  - Tests summary:");
            
            // Log details of first few tests
            for (int i = 0; i < Math.min(codeVersionDetails.tests().size(), 3); i++) {
                var test = codeVersionDetails.tests().get(i);
                log.info("    * Test {}: ID='{}', Input='{}', Expected='{}'", 
                        i + 1, test.id(),
                        test.input().length() > 30 ? test.input().substring(0, 30) + "..." : test.input(),
                        test.expectedOutput().length() > 30 ? test.expectedOutput().substring(0, 30) + "..." : test.expectedOutput());
            }

            // 3. Submit solution for execution using CodeRunner microservice
            log.info("🚀 Step 3: Submitting to CodeRunner via gRPC...");
            log.info("📦 Preparing gRPC execution request:");
            log.info("  - Challenge ID: '{}'", existingSolution.getChallengeId().id().toString());
            log.info("  - Code Version ID: '{}'", existingSolution.getCodeVersionId().id().toString());
            log.info("  - Student ID: '{}'", command.studentId().id().toString());
            log.info("  - Code: {} characters", command.code().length());
            log.info("  - Tests to validate: {}", codeVersionDetails.tests().size());
            
            var executionResult = codeRunnerExecutionService.executeSolution(
                existingSolution.getChallengeId().id().toString(),
                existingSolution.getCodeVersionId().id().toString(),
                command.studentId().id().toString(),
                command.code(),
                codeVersionDetails.tests()
            );

            // 📊 LOG RESULTADO FINAL
            log.info("🎉 Step 4: Processing execution results...");
            log.info("📊 Final execution summary:");
            log.info("  - Completed: {}", executionResult.successful());
            log.info("  - Total Tests: {}", executionResult.totalTests());
            log.info("  - Passed Tests: {}", executionResult.passedTests());
            log.info("  - Failed Tests: {}", executionResult.failedTests());
            log.info("  - Success Rate: {:.1f}%", executionResult.getSuccessRate());
            log.info("  - Execution Time: {} ms", executionResult.timeTaken());
            log.info("  - Message: {}", executionResult.message());
            log.info("  - Approved Test IDs: {}", executionResult.passedTestsId());
            
            if (executionResult.hasErrors()) {
                log.warn("⚠️ Execution encountered errors:");
                log.warn("  - Error Type: {}", executionResult.errorType());
                log.warn("  - Error Message: {}", executionResult.errorMessage());
            }

            // Create solution report with approved test IDs
            var approvedTestIds = executionResult.passedTestsId();
            
            log.info("✅ Solution executed via CodeRunner!");
            log.info("📋 Creating solution report...");
            
            // Get challenge details to calculate score
            log.info("📋 Step 5: Fetching challenge details for score calculation...");
            var challenge = externalChallengesService.getChallengeForScoring(
                existingSolution.getChallengeId().id().toString()
            );

            log.info("✅ Challenge details retrieved:");
            log.info("  - Challenge ID: '{}'", challenge.challengeId());
            log.info("  - Max Experience Points: {}", challenge.experiencePoints());
            log.info("  - Difficulty: {}", challenge.difficulty());

            // Calculate time taken to solve the challenge (in seconds)
            long timeTakenSeconds = calculateTimeTaken(existingSolution);

            log.info("⏱️ Time tracking:");
            log.info("  - Challenge started at: {}", existingSolution.getCreatedAt());
            log.info("  - Challenge completed at: {}", existingSolution.getLastAttemptAt());
            log.info("  - Time taken: {} seconds ({} minutes)", timeTakenSeconds, timeTakenSeconds / 60);

            // Calculate score with time-based penalties
            ScoringResult scoringResult = timeBasedScoringStrategy.calculateScore(
                challenge.experiencePoints(),
                challenge.difficulty(),
                timeTakenSeconds,
                executionResult.successful()
            );

            log.info("💯 Score calculated with time-based penalties:");
            log.info("  - Base Score: {}", scoringResult.baseScore());
            log.info("  - Score Multiplier: {}%", scoringResult.scoreMultiplier());
            log.info("  - Penalty Applied: {}", scoringResult.penaltyApplied());
            log.info("  - Points Earned: {}/{}", scoringResult.finalScore(), challenge.experiencePoints());
            log.info("  - Success Rate: {:.1f}%", executionResult.getSuccessRate());
            log.info("  - Time Performance: {} (formatted: {})",
                scoringResult.getTimeTakenMinutes() + " min",
                scoringResult.getFormattedTime());

            // Assign score to solution
            existingSolution.assignScore(scoringResult.finalScore(), challenge.experiencePoints());
            solutionRepository.save(existingSolution);
            log.info("✅ Score saved to solution");

            // Publish event to Kafka if student earned points
            if (scoringResult.finalScore() > 0) {
                log.info("📤 Publishing ChallengeCompletedEvent to Kafka...");

                // Generate scoring reason explanation
                String scoringReason = scoringResult.getScoringReason(challenge.difficulty().name());

                var event = new ChallengeCompletedEvent(
                    command.studentId().id().toString(),
                    existingSolution.getChallengeId().id().toString(),
                    existingSolution.getId().id().toString(),
                    scoringResult.finalScore(),
                    challenge.experiencePoints(),
                    executionResult.passedTests(),
                    executionResult.totalTests(),
                    executionResult.successful(),
                    executionResult.timeTaken(), // Code execution time (NOT solution time)
                    timeTakenSeconds, // Time taken to solve the challenge
                    scoringResult.scoreMultiplier(),
                    scoringResult.penaltyApplied(),
                    scoringReason,
                    LocalDateTime.now()
                );

                log.info("📊 Event details:");
                log.info("  - Score Multiplier: {}%", scoringResult.scoreMultiplier());
                log.info("  - Penalty Applied: {}", scoringResult.penaltyApplied());
                log.info("  - Scoring Reason: {}", scoringReason);

                kafkaProducerService.publishChallengeCompleted(event);
                log.info("✅ Event published successfully");
            } else {
                log.info("⚠️ No points earned, event not published");
            }

            // TODO: Create actual SolutionReport entity with approved test IDs and execution metadata
            var solutionReportId = new SolutionReportId(UUID.randomUUID());
            log.info("  - Solution Report ID: '{}'", solutionReportId.value());

            // Enhanced message with execution details, score, and time-based penalty info
            String message = String.format(
                "Solution executed via CodeRunner. %s. %d out of %d tests passed (%.1f%%). Score: %d/%d points (%.0f%% with time penalty). Time taken: %s. Execution time: %d ms",
                executionResult.message(),
                executionResult.passedTests(),
                executionResult.totalTests(),
                executionResult.getSuccessRate(),
                scoringResult.finalScore(),
                challenge.experiencePoints(),
                (double) scoringResult.scoreMultiplier(),
                scoringResult.getFormattedTime(),
                executionResult.timeTaken()
            );

            log.info("🎯 =============== SUBMIT SOLUTION PROCESS COMPLETED ===============");

            return SubmissionResult.success(
                solutionReportId,
                approvedTestIds,
                executionResult.totalTests(),
                message,
                String.format("Execution completed in %d ms. Score: %d/%d points (%d%% time multiplier applied)",
                    executionResult.timeTaken(), scoringResult.finalScore(), challenge.experiencePoints(),
                    scoringResult.scoreMultiplier()),
                executionResult.timeTaken()
            );
            
        } catch (Exception e) {
            log.error("💥 =============== SUBMIT SOLUTION PROCESS ERROR ===============");
            log.error("❌ Error executing solution {}: {}", command.solutionId().id(), e.getMessage(), e);
            return SubmissionResult.failure("Error during code execution: " + e.getMessage());
        }
    }


    /**
     * Handles the UpdateSolutionCommand to modify a solution's code.
     * 
     * <p>This handler follows CQRS principles by separating command execution
     * from query logic. It performs the following steps:</p>
     * 
     * <ol>
     *   <li>Validate that the solution exists (query side)</li>
     *   <li>Retrieve the solution aggregate from repository</li>
     *   <li>Delegate business logic to the aggregate's domain method</li>
     *   <li>Persist the updated aggregate state</li>
     * </ol>
     * 
     * <h3>Business Rules Enforced:</h3>
     * <ul>
     *   <li>Solution must exist (throws exception if not found)</li>
     *   <li>Code update is delegated to Solution aggregate (domain logic)</li>
     *   <li>Transaction boundaries ensure consistency</li>
     * </ul>
     * 
     * <h3>Domain Events:</h3>
     * <p>This operation may trigger domain events (if implemented):</p>
     * <ul>
     *   <li>SolutionCodeUpdatedEvent - when code is successfully changed</li>
     * </ul>
     * 
     * @param command The validated command containing solution ID and new code
     * @throws IllegalArgumentException if solution is not found
     * @throws IllegalStateException if solution is in a state that prevents updates
     * 
     * @see UpdateSolutionCommand
     * @see com.levelupjourney.microservicechallenges.solutions.domain.model.aggregates.Solution#updateCode(String)
     */
    @Override
    @Transactional
    public void handle(UpdateSolutionCommand command) {
        try {
            // Step 1: Query for solution existence (CQRS: using query service)
            var query = new GetSolutionByIdQuery(command.solutionId());
            var solutionOptional = solutionQueryService.handle(query);

            // Step 2: Validate solution exists
            if (solutionOptional.isEmpty()) {
                throw new IllegalArgumentException(
                    String.format("Solution not found with ID: %s", command.solutionId().id())
                );
            }
            
            var solution = solutionOptional.get();
            
            // Step 3: Delegate to aggregate's domain method
            solution.updateCode(command.code());
            
            // Step 4: Persist the aggregate
            solutionRepository.save(solution);
            
            
        } catch (IllegalArgumentException e) {
            throw e; // Re-throw to be handled by controller
            
        } catch (Exception e) {
            throw new IllegalStateException(
                String.format("Failed to update solution: %s", e.getMessage()), 
                e
            );
        }
    }


    /**
     * Calculate the time taken to solve the challenge (in seconds).
     * Time is measured from when the solution was created (challenge started)
     * to when the last submission attempt was made.
     *
     * @param solution The solution aggregate
     * @return Time taken in seconds
     */
    private long calculateTimeTaken(Solution solution) {
        if (solution.getLastAttemptAt() == null || solution.getCreatedAt() == null) {
            log.warn("⚠️ Unable to calculate time taken: missing timestamps");
            return 0L;
        }

        long createdAtMillis = solution.getCreatedAt().getTime();
        long lastAttemptMillis = solution.getLastAttemptAt().getTime();
        long timeTakenMillis = lastAttemptMillis - createdAtMillis;

        // Convert milliseconds to seconds
        return timeTakenMillis / 1000;
    }
}
