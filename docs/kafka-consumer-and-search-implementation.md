# Kafka Consumer & Search Endpoint - Implementation Summary

## 📋 Resumen de Cambios

### 1. Consumidor Kafka para Eventos de Guías
- **Tópico**: `guides.challenge.added.v1`
- **Evento**: `GuideChallengeAddedEvent`
- **Propósito**: Escuchar cuando un challenge es agregado a una guía desde el Learning Service

### 2. Configuración Dinámica de Kafka
- **Variable de Entorno**: `IS_AZURE`
  - `true`: Usa Azure Event Hubs con SASL_SSL
  - `false`: Usa Kafka estándar con PLAINTEXT
- **Aplicable a**: Producer y Consumer

### 3. Endpoint de Búsqueda
- **Ruta**: `GET /api/v1/challenges/search`
- **Filtros**: name, difficulty, tags
- **Restricción**: Solo retorna challenges con estado `PUBLISHED`

---

## 🔧 Archivos Creados

### 1. Event Model
```
src/main/java/com/levelupjourney/microservicechallenges/shared/domain/model/events/GuideChallengeAddedEvent.java
```

### 2. Kafka Consumer
```
src/main/java/com/levelupjourney/microservicechallenges/shared/infrastructure/messaging/kafka/GuideChallengeEventConsumer.java
```

### 3. Kafka Consumer Config
```
src/main/java/com/levelupjourney/microservicechallenges/shared/infrastructure/messaging/kafka/KafkaConsumerConfig.java
```

### 4. Search Query
```
src/main/java/com/levelupjourney/microservicechallenges/challenges/domain/model/queries/SearchPublishedChallengesQuery.java
```

---

## 🔄 Archivos Modificados

### 1. `application.yml`
```yaml
kafka:
  is-azure: ${IS_AZURE:false}  # Nueva variable
  topics:
    challenge-completed: ${KAFKA_TOPIC_CHALLENGE_COMPLETED:challenge.completed}
    guide-challenge-added: ${KAFKA_TOPIC_GUIDE_CHALLENGE_ADDED:guides.challenge.added.v1}  # Nuevo tópico
```

### 2. `KafkaProducerConfig.java`
- Añadido log de configuración basado en `IS_AZURE`
- Diferencia entre Azure Event Hubs y Kafka estándar

### 3. `ChallengeRepository.java`
- Nuevos métodos de búsqueda:
  - `searchPublishedChallengesByName(String name)`
  - `searchPublishedChallengesByDifficulty(Difficulty difficulty)`
  - `searchPublishedChallengesByNameAndDifficulty(String name, Difficulty difficulty)`

### 4. `ChallengeQueryService.java` y `ChallengeQueryServiceImpl.java`
- Añadido handler para `SearchPublishedChallengesQuery`
- Implementa lógica de filtrado combinado

### 5. `ChallengeController.java`
- Nuevo endpoint: `GET /api/v1/challenges/search`

---

## 🚀 Configuración de Variables de Entorno

### Para Azure Event Hubs (Producción)
```bash
# .env
IS_AZURE=true
KAFKA_BOOTSTRAP_SERVERS=your-eventhub-namespace.servicebus.windows.net:9093
KAFKA_SECURITY_PROTOCOL=SASL_SSL
KAFKA_SASL_MECHANISM=PLAIN
KAFKA_SASL_JAAS_CONFIG=org.apache.kafka.common.security.plain.PlainLoginModule required username="$ConnectionString" password="Endpoint=sb://your-eventhub-namespace.servicebus.windows.net/;SharedAccessKeyName=YOUR_KEY_NAME;SharedAccessKey=YOUR_KEY;EntityPath=guides.challenge.added.v1";

# Tópicos
KAFKA_TOPIC_CHALLENGE_COMPLETED=challenge.completed
KAFKA_TOPIC_GUIDE_CHALLENGE_ADDED=guides.challenge.added.v1
```

### Para Kafka Local (Desarrollo)
```bash
# .env
IS_AZURE=false
KAFKA_BOOTSTRAP_SERVERS=localhost:9092

# Tópicos (opcionales, usa defaults)
KAFKA_TOPIC_CHALLENGE_COMPLETED=challenge.completed
KAFKA_TOPIC_GUIDE_CHALLENGE_ADDED=guides.challenge.added.v1
```

---

## 📡 Consumidor Kafka - Detalles

### Evento Recibido
```json
{
  "guideId": "550e8400-e29b-41d4-a716-446655440000",
  "challengeId": "123e4567-e89b-12d3-a456-426614174000",
  "occurredAt": "2025-11-10T05:20:00Z"
}
```

### Logs del Consumer
```
📥 Received GuideChallengeAddedEvent: guideId=550e8400-e29b-41d4-a716-446655440000, challengeId=123e4567-e89b-12d3-a456-426614174000, occurredAt=2025-11-10T05:20:00Z
✅ Successfully processed GuideChallengeAddedEvent for challengeId=123e4567-e89b-12d3-a456-426614174000
```

### Configuración del Listener
- **Group ID**: `challenges-service` (nombre de la aplicación)
- **Auto Offset Reset**: `earliest` (lee desde el inicio si es nuevo consumer)
- **Auto Commit**: `true` (commits automáticos cada 1 segundo)

### TODO en GuideChallengeEventConsumer
El método `handleGuideChallengeAdded()` actualmente solo loggea el evento. Implementa la lógica de negocio necesaria:
- Actualizar estadísticas del challenge
- Disparar notificaciones
- Actualizar índices de búsqueda
- Guardar relación guía-challenge si es necesario

---

## 🔍 Endpoint de Búsqueda

### URL Base
```
GET /api/v1/challenges/search
```

### Parámetros de Consulta (Query Params)

| Parámetro | Tipo | Requerido | Descripción | Ejemplo |
|-----------|------|-----------|-------------|---------|
| `name` | String | No | Búsqueda parcial case-insensitive del nombre | `hello` |
| `difficulty` | String | No | Filtrar por dificultad (EASY, MEDIUM, HARD) | `EASY` |
| `tags` | String | No | Filtrar por tags (separados por comas) | `loops,arrays` |

### Ejemplos de Uso

#### 1. Buscar por nombre
```http
GET /api/v1/challenges/search?name=hello
```

**Resultado**: Todos los challenges PUBLISHED cuyo nombre contenga "hello" (ej: "Hello World", "Say Hello", "hello-program")

#### 2. Buscar por nombre parcial
```http
GET /api/v1/challenges/search?name=h
```

**Resultado**: Todos los challenges PUBLISHED cuyo nombre contenga "h"

#### 3. Buscar por dificultad
```http
GET /api/v1/challenges/search?difficulty=EASY
```

**Resultado**: Todos los challenges PUBLISHED con dificultad EASY

#### 4. Combinar nombre y dificultad
```http
GET /api/v1/challenges/search?name=sort&difficulty=MEDIUM
```

**Resultado**: Challenges PUBLISHED que contengan "sort" en el nombre Y sean de dificultad MEDIUM

#### 5. Buscar por tags
```http
GET /api/v1/challenges/search?tags=arrays,loops
```

**Resultado**: Challenges PUBLISHED que tengan al menos uno de los tags "arrays" o "loops"

#### 6. Combinar todos los filtros
```http
GET /api/v1/challenges/search?name=find&difficulty=HARD&tags=algorithms,binary-search
```

**Resultado**: Challenges PUBLISHED que:
- Contengan "find" en el nombre
- Sean de dificultad HARD
- Tengan al menos uno de los tags "algorithms" o "binary-search"

#### 7. Sin filtros (retorna todos los publicados)
```http
GET /api/v1/challenges/search
```

**Resultado**: Todos los challenges PUBLISHED (igual que `GET /api/v1/challenges`)

### Respuesta
```json
[
  {
    "id": "123e4567-e89b-12d3-a456-426614174000",
    "name": "Hello World in Python",
    "description": "Create a program that prints Hello World",
    "status": "PUBLISHED",
    "difficulty": "EASY",
    "experiencePoints": 50,
    "teacherId": "550e8400-e29b-41d4-a716-446655440000",
    "tags": ["basics", "python", "printing"],
    "stars": [],
    "guides": [],
    "createdAt": "2025-11-10T05:00:00Z",
    "updatedAt": "2025-11-10T05:00:00Z"
  }
]
```

### Códigos de Estado
- `200 OK`: Búsqueda exitosa (puede retornar lista vacía)
- `400 Bad Request`: Parámetros inválidos

### Características
- ✅ Búsqueda **case-insensitive** para nombres
- ✅ Búsqueda **parcial** (LIKE) para nombres
- ✅ Filtrado **combinado** (todos los filtros se aplican como AND)
- ✅ Tags permiten **OR** (al menos uno debe coincidir)
- ✅ Solo retorna challenges con estado **PUBLISHED**

---

## 🧪 Testing

### Test Manual con cURL

#### Buscar por nombre
```bash
curl -X GET "http://localhost:8083/api/v1/challenges/search?name=hello" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

#### Buscar por dificultad
```bash
curl -X GET "http://localhost:8083/api/v1/challenges/search?difficulty=EASY" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

#### Buscar combinando filtros
```bash
curl -X GET "http://localhost:8083/api/v1/challenges/search?name=sort&difficulty=MEDIUM&tags=arrays" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Publicar Evento de Prueba (para testing del consumer)

Si tienes acceso a Kafka/Azure Event Hubs, publica este mensaje al tópico `guides.challenge.added.v1`:

```json
{
  "guideId": "550e8400-e29b-41d4-a716-446655440000",
  "challengeId": "123e4567-e89b-12d3-a456-426614174000",
  "occurredAt": "2025-11-10T05:20:00Z"
}
```

Verifica los logs del microservicio para confirmar que el evento fue procesado.

---

## 📊 Swagger Documentation

El nuevo endpoint está automáticamente documentado en Swagger UI:

```
http://localhost:8083/swagger-ui/index.html
```

Busca la sección **"Challenges"** y encontrarás:
- `GET /api/v1/challenges/search` - Search published challenges

---

## ✅ Checklist de Implementación

- [x] Crear evento `GuideChallengeAddedEvent`
- [x] Crear consumidor Kafka `GuideChallengeEventConsumer`
- [x] Configurar `KafkaConsumerConfig` con soporte para IS_AZURE
- [x] Actualizar `KafkaProducerConfig` con logs dinámicos
- [x] Añadir variable `IS_AZURE` a application.yml
- [x] Añadir tópico `guide-challenge-added` a configuration
- [x] Crear query `SearchPublishedChallengesQuery`
- [x] Extender `ChallengeRepository` con métodos de búsqueda
- [x] Implementar handler en `ChallengeQueryServiceImpl`
- [x] Añadir endpoint `/search` en `ChallengeController`
- [x] Compilación exitosa
- [ ] Testing del consumer con evento real
- [ ] Testing del endpoint de búsqueda
- [ ] Implementar lógica de negocio en el consumer (TODO)

---

## 🎯 Próximos Pasos

1. **Implementar Lógica de Negocio en el Consumer**
   - Decidir qué hacer cuando un challenge es agregado a una guía
   - Posibles acciones: actualizar contadores, notificaciones, sincronización

2. **Testing del Consumer**
   - Publicar eventos de prueba desde Learning Service
   - Verificar logs y comportamiento

3. **Testing del Endpoint de Búsqueda**
   - Crear challenges de prueba
   - Probar todos los filtros
   - Verificar que solo retorna PUBLISHED

4. **Monitoreo**
   - Añadir métricas para el consumer
   - Logs estructurados para debugging
   - Dead Letter Queue para eventos fallidos

---

## 🔐 Seguridad

- El endpoint de búsqueda requiere autenticación JWT (heredado del controlador)
- Solo retorna challenges PUBLISHED (públicos)
- Los filtros son validados antes de la consulta
- El consumer usa group ID del application name para evitar conflictos

---

## 📝 Notas Adicionales

### Diferencia entre Azure y Kafka Local

| Aspecto | Azure Event Hubs | Kafka Local |
|---------|-----------------|-------------|
| Protocolo | SASL_SSL | PLAINTEXT |
| Puerto | 9093 | 9092 |
| Autenticación | Connection String | Ninguna |
| IS_AZURE | true | false |

### Performance

- Las búsquedas por **name** y **difficulty** usan queries de BD optimizadas
- El filtrado por **tags** se hace en memoria (considera indexar si hay muchos challenges)
- Considera añadir paginación si el dataset crece significativamente
