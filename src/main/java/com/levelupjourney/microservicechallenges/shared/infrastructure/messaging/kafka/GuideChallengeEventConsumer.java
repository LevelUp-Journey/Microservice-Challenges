package com.levelupjourney.microservicechallenges.shared.infrastructure.messaging.kafka;

import com.levelupjourney.microservicechallenges.shared.domain.model.events.GuideChallengeAddedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Kafka consumer that listens to guide-related events from the Learning Service.
 * Processes events when challenges are added to guides.
 */
@Slf4j
@Component
public class GuideChallengeEventConsumer {

    /**
     * Handles the event when a challenge is added to a guide.
     * This consumer listens to the "guides.challenge.added.v1" topic.
     *
     * @param event The event containing guide and challenge IDs
     */
    @KafkaListener(
        topics = "${kafka.topics.guide-challenge-added:guides.challenge.added.v1}",
        groupId = "${spring.application.name:challenges-service}",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleGuideChallengeAdded(GuideChallengeAddedEvent event) {
        log.info("📥 Received GuideChallengeAddedEvent: guideId={}, challengeId={}, occurredAt={}",
            event.getGuideId(),
            event.getChallengeId(),
            event.getOccurredAt());

        try {
            // TODO: Implement business logic here
            // Examples:
            // - Update challenge statistics (e.g., increment guide count)
            // - Trigger notifications
            // - Update search indexes
            // - Store guide-challenge relationship if needed locally
            
            log.info("✅ Successfully processed GuideChallengeAddedEvent for challengeId={}", event.getChallengeId());
        } catch (Exception e) {
            log.error("❌ Error processing GuideChallengeAddedEvent: guideId={}, challengeId={}",
                event.getGuideId(),
                event.getChallengeId(),
                e);
            // TODO: Implement error handling strategy (e.g., retry, dead letter queue)
            throw e; // Re-throw to trigger Kafka retry mechanism
        }
    }
}
