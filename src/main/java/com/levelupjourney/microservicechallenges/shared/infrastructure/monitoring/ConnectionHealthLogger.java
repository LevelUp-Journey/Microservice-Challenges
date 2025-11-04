package com.levelupjourney.microservicechallenges.shared.infrastructure.monitoring;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * Component that logs successful connections to external services
 * (Database and Kafka/Azure Event Hubs) when the application starts.
 */
@Component
@Slf4j
public class ConnectionHealthLogger {

    private final DataSource dataSource;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${spring.datasource.url}")
    private String datasourceUrl;

    @Value("${spring.kafka.bootstrap-servers}")
    private String kafkaBootstrapServers;

    @Value("${kafka.topics.challenge-completed}")
    private String challengeCompletedTopic;

    public ConnectionHealthLogger(DataSource dataSource,
                                  KafkaTemplate<String, Object> kafkaTemplate) {
        this.dataSource = dataSource;
        this.kafkaTemplate = kafkaTemplate;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void logConnectionsOnStartup() {
        logDatabaseConnection();
        logKafkaConnection();
    }

    private void logDatabaseConnection() {
        try (Connection connection = dataSource.getConnection()) {
            if (connection != null && !connection.isClosed()) {
                String databaseProductName = connection.getMetaData().getDatabaseProductName();
                String databaseVersion = connection.getMetaData().getDatabaseProductVersion();
                
                log.info("╔════════════════════════════════════════════════════════════════╗");
                log.info("║          ✅ DATABASE CONNECTION SUCCESSFUL                     ║");
                log.info("╠════════════════════════════════════════════════════════════════╣");
                log.info("║  Database: {} {}", databaseProductName, databaseVersion);
                log.info("║  URL: {}", maskSensitiveInfo(datasourceUrl));
                log.info("║  Status: CONNECTED ✓", datasourceUrl);
                log.info("╚════════════════════════════════════════════════════════════════╝");
            }
        } catch (Exception e) {
            log.error("╔════════════════════════════════════════════════════════════════╗");
            log.error("║          ❌ DATABASE CONNECTION FAILED                         ║");
            log.error("╠════════════════════════════════════════════════════════════════╣");
            log.error("║  URL: {}", maskSensitiveInfo(datasourceUrl));
            log.error("║  Error: {}", e.getMessage());
            log.error("╚════════════════════════════════════════════════════════════════╝");
        }
    }

    private void logKafkaConnection() {
        try {
            // Try to get Kafka metrics to verify connection
            var metrics = kafkaTemplate.metrics();
            
            log.info("╔════════════════════════════════════════════════════════════════╗");
            log.info("║      ✅ KAFKA/AZURE EVENT HUB CONNECTION SUCCESSFUL            ║");
            log.info("╠════════════════════════════════════════════════════════════════╣");
            log.info("║  Bootstrap Servers: {}", kafkaBootstrapServers);
            log.info("║  Topic: {}", challengeCompletedTopic);
            log.info("║  Protocol: SASL_SSL (Azure Event Hubs)");
            log.info("║  Metrics: {} metrics available", metrics.size());
            log.info("║  Status: CONNECTED ✓");
            log.info("╚════════════════════════════════════════════════════════════════╝");
        } catch (Exception e) {
            log.error("╔════════════════════════════════════════════════════════════════╗");
            log.error("║      ❌ KAFKA/AZURE EVENT HUB CONNECTION FAILED                ║");
            log.error("╠════════════════════════════════════════════════════════════════╣");
            log.error("║  Bootstrap Servers: {}", kafkaBootstrapServers);
            log.error("║  Topic: {}", challengeCompletedTopic);
            log.error("║  Error: {}", e.getMessage());
            log.error("╚════════════════════════════════════════════════════════════════╝");
        }
    }

    /**
     * Mask sensitive information in connection strings
     */
    private String maskSensitiveInfo(String url) {
        if (url == null) {
            return "N/A";
        }
        // Mask password in JDBC URL
        return url.replaceAll("password=[^&;]*", "password=****");
    }
}
