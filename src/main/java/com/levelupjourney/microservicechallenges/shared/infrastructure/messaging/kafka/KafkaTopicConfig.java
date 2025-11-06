package com.levelupjourney.microservicechallenges.shared.infrastructure.messaging.kafka;

import org.springframework.context.annotation.Configuration;

/**
 * Kafka Topic Configuration.
 * 
 * NOTA IMPORTANTE PARA AZURE EVENT HUBS:
 * - Azure Event Hubs NO soporta la creación automática de topics vía Kafka Admin API
 * - Los Event Hubs (topics) deben ser creados manualmente en Azure Portal
 * - Esta clase se mantiene para documentación pero no define beans activos
 * 
 * Para crear el topic en Azure:
 * 1. Ve a Azure Portal
 * 2. Navega a tu Event Hubs Namespace
 * 3. Crea un Event Hub llamado "challenge.completed"
 * 4. Configura el número de particiones deseado (recomendado: 3 o más)
 */
@Configuration
public class KafkaTopicConfig {
    // No se requieren beans para Azure Event Hubs
    // Los topics se gestionan manualmente en Azure Portal
}
