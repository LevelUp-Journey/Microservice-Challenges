package com.levelupjourney.microservicechallenges.shared.infrastructure.messaging.kafka;

import org.springframework.context.annotation.Configuration;

/**
 * Kafka Producer Configuration
 * 
 * La configuración de Kafka ahora se maneja completamente a través de application.yml
 * usando las propiedades spring.kafka.* que Spring Boot configura automáticamente.
 * 
 * Esto incluye:
 * - spring.kafka.bootstrap-servers
 * - spring.kafka.properties.security.protocol (SASL_SSL para Azure Event Hubs)
 * - spring.kafka.properties.sasl.mechanism (PLAIN)
 * - spring.kafka.properties.sasl.jaas.config (Connection String de Azure)
 * - spring.kafka.producer.* (serializadores y propiedades del producer)
 */
@Configuration
public class KafkaProducerConfig {
    // Spring Boot auto-configura el KafkaTemplate basado en spring.kafka.* properties
    // No se requiere configuración manual adicional
}
