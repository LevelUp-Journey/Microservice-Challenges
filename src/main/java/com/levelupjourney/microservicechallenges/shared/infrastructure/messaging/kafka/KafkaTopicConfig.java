package com.levelupjourney.microservicechallenges.shared.infrastructure.messaging.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/**
 * Kafka Topic Configuration.
 * Defines topics with explicit partitions and replication settings.
 * This ensures topics are created with proper configuration when the application starts.
 */
@Configuration
public class KafkaTopicConfig {

    @Value("${kafka.topics.challenge-completed}")
    private String challengeCompletedTopic;

    /**
     * Configure the challenge.completed topic.
     * - Partitions: 3 (allows parallel processing by consumer group)
     * - Replicas: 1 (for development; increase in production for fault tolerance)
     * - Retention: 7 days (events older than this will be deleted)
     *
     * @return NewTopic configuration
     */
    @Bean
    public NewTopic challengeCompletedTopic() {
        return TopicBuilder.name(challengeCompletedTopic)
                .partitions(3)
                .replicas(1)
                .config("retention.ms", "604800000") // 7 days in milliseconds
                .build();
    }
}
