package com.levelupjourney.microservicechallenges.solutions.application.internal.commandservices;

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

    public SolutionCommandServiceImpl(ExternalChallengesService externalChallengesService,
                                    SolutionQueryService solutionQueryService,
                                    SolutionRepository solutionRepository,
                                    CodeRunnerExecutionService codeRunnerExecutionService,
                                    KafkaProducerService kafkaProducerService) {
        this.externalChallengesService = externalChallengesService;
        this.solutionQueryService = solutionQueryService;
        this.solutionRepository = solutionRepository;
        this.codeRunnerExecutionService = codeRunnerExecutionService;
        this.kafkaProducerService = kafkaProducerService;
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
        log.info("  - Current status: '{}'", existingSolution.getDetails().getStatus());

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

            // Calculate score based on test results
            int pointsEarned = calculateScore(
                challenge.experiencePoints(),
                executionResult.passedTests(),
                executionResult.totalTests(),
                executionResult.successful()
            );

            log.info("💯 Score calculated:");
            log.info("  - Points Earned: {}/{}", pointsEarned, challenge.experiencePoints());
            log.info("  - Success Rate: {:.1f}%", executionResult.getSuccessRate());

            // Assign score to solution
            existingSolution.assignScore(pointsEarned, challenge.experiencePoints());
            solutionRepository.save(existingSolution);
            log.info("✅ Score saved to solution");

            // Publish event to Kafka if student earned points
            if (pointsEarned > 0) {
                log.info("📤 Publishing ChallengeCompletedEvent to Kafka...");
                var event = new ChallengeCompletedEvent(
                    command.studentId().id().toString(),
                    existingSolution.getChallengeId().id().toString(),
                    existingSolution.getId().id().toString(),
                    pointsEarned,
                    challenge.experiencePoints(),
                    executionResult.passedTests(),
                    executionResult.totalTests(),
                    executionResult.successful(),
                    executionResult.timeTaken(),
                    LocalDateTime.now()
                );

                kafkaProducerService.publishChallengeCompleted(event);
                log.info("✅ Event published successfully");
            } else {
                log.info("⚠️ No points earned, event not published");
            }

            // TODO: Create actual SolutionReport entity with approved test IDs and execution metadata
            var solutionReportId = new SolutionReportId(UUID.randomUUID());
            log.info("  - Solution Report ID: '{}'", solutionReportId.value());

            // Enhanced message with execution details and score
            String message = String.format(
                "Solution executed via CodeRunner. %s. %d out of %d tests passed (%.1f%%). Score: %d/%d points. Execution time: %d ms",
                executionResult.message(),
                executionResult.passedTests(),
                executionResult.totalTests(),
                executionResult.getSuccessRate(),
                pointsEarned,
                challenge.experiencePoints(),
                executionResult.timeTaken()
            );

            log.info("🎯 =============== SUBMIT SOLUTION PROCESS COMPLETED ===============");

            return SubmissionResult.success(
                solutionReportId,
                approvedTestIds,
                executionResult.totalTests(),
                message,
                String.format("Execution completed in %d ms. Score: %d/%d points",
                    executionResult.timeTaken(), pointsEarned, challenge.experiencePoints()),
                executionResult.timeTaken()
            );
            
        } catch (Exception e) {
            log.error("💥 =============== SUBMIT SOLUTION PROCESS ERROR ===============");
            log.error("❌ Error executing solution {}: {}", command.solutionId().id(), e.getMessage(), e);
            return SubmissionResult.failure("Error during code execution: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void handle(UpdateSolutionCommand command) {
        var solution = solutionQueryService.handle(
                new GetSolutionByIdQuery(command.solutionId())
        );

        if (solution.isEmpty()) {
            throw new IllegalArgumentException("Solution not found: " + command.solutionId().id());
        }

        var existingSolution = solution.get();
        existingSolution.updateSolution(command.code());
        solutionRepository.save(existingSolution);
    }

    /**
     * Calculate the score earned based on test results.
     * Strategy: Only award full points if ALL tests pass. Otherwise, award proportional points.
     *
     * @param maxPoints Maximum points available for the challenge
     * @param passedTests Number of tests that passed
     * @param totalTests Total number of tests
     * @param allPassed Whether all tests passed
     * @return Points earned (0 to maxPoints)
     */
    private int calculateScore(Integer maxPoints, int passedTests, int totalTests, boolean allPassed) {
        if (maxPoints == null || maxPoints == 0) {
            return 0;
        }

        if (totalTests == 0) {
            return 0;
        }

        // Strategy 1: Full points only if all tests pass (recommended for competitive environment)
        if (allPassed) {
            return maxPoints;
        }

        // Strategy 2: Proportional scoring (award partial credit)
        // Uncomment this if you want to award proportional points even when not all tests pass
        // return (maxPoints * passedTests) / totalTests;

        // Default: No points if not all tests pass
        return 0;
    }
}
