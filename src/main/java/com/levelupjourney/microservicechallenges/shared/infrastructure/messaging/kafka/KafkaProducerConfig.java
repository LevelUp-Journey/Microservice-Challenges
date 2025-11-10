package com.levelupjourney.microservicechallenges.shared.infrastructure.messaging.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

/**
 * Kafka Producer Configuration
 * 
 * La configuración de Kafka se maneja a través de application.yml con soporte
 * para Azure Event Hubs (SASL_SSL) y Kafka estándar (PLAINTEXT).
 * El modo se controla mediante la variable de entorno IS_AZURE.
 * 
 * Propiedades configuradas:
 * - spring.kafka.bootstrap-servers
 * - spring.kafka.properties.security.protocol (SASL_SSL para Azure, PLAINTEXT para Kafka estándar)
 * - spring.kafka.properties.sasl.mechanism (PLAIN para Azure)
 * - spring.kafka.properties.sasl.jaas.config (Connection String de Azure)
 * - spring.kafka.producer.* (serializadores y propiedades del producer)
 */
@Slf4j
@Configuration
public class KafkaProducerConfig {
    
    @Value("${kafka.is-azure:false}")
    private boolean isAzure;
    
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;
    
    @PostConstruct
    public void logConfiguration() {
        if (isAzure) {
            log.info("🔐 Kafka Producer configured for Azure Event Hubs (SASL_SSL) at {}", bootstrapServers);
        } else {
            log.info("🔓 Kafka Producer configured for standard Kafka (PLAINTEXT) at {}", bootstrapServers);
        }
    }
    
    // Spring Boot auto-configura el KafkaTemplate basado en spring.kafka.* properties
}
