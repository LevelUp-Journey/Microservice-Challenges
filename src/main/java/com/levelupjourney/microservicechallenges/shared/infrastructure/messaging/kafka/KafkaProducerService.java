package com.levelupjourney.microservicechallenges.shared.infrastructure.messaging.kafka;

import com.levelupjourney.microservicechallenges.solutions.domain.model.events.ChallengeCompletedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**
 * Kafka Producer Service for publishing domain events to Kafka topics.
 * Handles event serialization and publishing with proper error handling and logging.
 */
@Service
@Slf4j
public class KafkaProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topics.challenge-completed}")
    private String challengeCompletedTopic;

    @Autowired
    public KafkaProducerService(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Publish a ChallengeCompletedEvent to Kafka.
     * The event will be consumed by the Profile Service to update student scores and rankings.
     *
     * @param event The challenge completed event containing score information
     */
    public void publishChallengeCompleted(ChallengeCompletedEvent event) {
        log.info("📤 Publishing ChallengeCompletedEvent to Azure Event Hub (Kafka):");
        log.info("  - Topic: '{}'", challengeCompletedTopic);
        log.info("  - Student ID: '{}'", event.getStudentId());
        log.info("  - Challenge ID: '{}'", event.getChallengeId());
        log.info("  - Points Earned: {}/{}", event.getExperiencePointsEarned(), event.getTotalExperiencePoints());
        log.info("  - Tests Passed: {}/{}", event.getPassedTests(), event.getTotalTests());

        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(
            challengeCompletedTopic,
            event.getStudentId(), // Use studentId as partition key for ordering
            event
        );

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("✅ Successfully published ChallengeCompletedEvent to Azure Event Hub:");
                log.info("  - Topic: {}", result.getRecordMetadata().topic());
                log.info("  - Partition: {}", result.getRecordMetadata().partition());
                log.info("  - Offset: {}", result.getRecordMetadata().offset());
                log.info("  - Timestamp: {}", result.getRecordMetadata().timestamp());
            } else {
                log.error("❌ Failed to publish ChallengeCompletedEvent to Azure Event Hub for student '{}': {}",
                    event.getStudentId(), ex.getMessage(), ex);
            }
        });
    }

    /**
     * Publish event synchronously (blocks until confirmation).
     * Use this when you need to ensure the event is published before continuing.
     *
     * @param event The challenge completed event
     * @return true if published successfully, false otherwise
     */
    public boolean publishChallengeCompletedSync(ChallengeCompletedEvent event) {
        try {
            log.info("Publishing ChallengeCompletedEvent synchronously to Kafka");
            SendResult<String, Object> result = kafkaTemplate.send(
                challengeCompletedTopic,
                event.getStudentId(),
                event
            ).get(); // Blocking call

            log.info("Successfully published ChallengeCompletedEvent (sync):");
            log.info("  - Partition: {}", result.getRecordMetadata().partition());
            log.info("  - Offset: {}", result.getRecordMetadata().offset());
            return true;

        } catch (Exception ex) {
            log.error("Failed to publish ChallengeCompletedEvent synchronously: {}", ex.getMessage(), ex);
            return false;
        }
    }
}
