# 🧪 Guía Completa de Testing - Flujo End-to-End

Esta guía te permitirá probar el flujo completo desde la creación de un challenge hasta el envío de puntos al microservicio de Profiles a través de Kafka.

## 📋 Tabla de Contenidos

1. [Prerequisitos](#prerequisitos)
2. [Flujo Completo](#flujo-completo)
3. [Endpoints Paso a Paso](#endpoints-paso-a-paso)
4. [Verificación del Flujo](#verificación-del-flujo)
5. [Troubleshooting](#troubleshooting)

---

## 🔧 Prerequisitos

Antes de comenzar, asegúrate de tener:

- ✅ **Microservicio Challenges** corriendo en `http://localhost:8082`
- ✅ **Microservicio CodeRunner** corriendo (gRPC)
- ✅ **Kafka** corriendo y accesible
- ✅ **Microservicio Profiles** corriendo y escuchando eventos de Kafka
- ✅ **PostgreSQL** corriendo con la base de datos configurada

---

## 🔄 Flujo Completo

```
1. CREATE CHALLENGE
   ↓
2. ADD CODE VERSION
   ↓
3. ADD TESTS (múltiples)
   ↓
4. PUBLISH CHALLENGE
   ↓
5. CREATE SOLUTION (estudiante)
   ↓
6. UPDATE SOLUTION (estudiante escribe código)
   ↓
7. SUBMIT SOLUTION
   ↓
   → CodeRunner (gRPC) → Ejecuta tests
   ↓
   → Calcula puntos
   ↓
   → Publica evento a Kafka
   ↓
   → Profile Service actualiza score
```

---

## 📡 Endpoints Paso a Paso

### **PASO 1: Crear un Challenge**

**Endpoint:** `POST /api/v1/challenges`

**Descripción:** Crea un nuevo challenge con puntos de experiencia.

**Request:**

```http
POST http://localhost:8082/api/v1/challenges
Content-Type: application/json

{
  "teacherId": "550e8400-e29b-41d4-a716-446655440000",
  "name": "Suma de Dos Números",
  "description": "Implementa una función que sume dos números enteros",
  "experiencePoints": 50,
  "difficulty": "EASY"
}
```

**Response Esperada:**

```json
{
  "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "teacherId": "550e8400-e29b-41d4-a716-446655440000",
  "name": "Suma de Dos Números",
  "description": "Implementa una función que sume dos números enteros",
  "experiencePoints": 50,
  "difficulty": "EASY",
  "status": "DRAFT",
  "createdAt": "2025-10-18T10:00:00"
}
```

> ⚠️ **IMPORTANTE:** Copia el `id` del response y úsalo como `{challengeId}` en los siguientes pasos.

---

### **PASO 2: Agregar Code Version**

**Endpoint:** `POST /api/v1/challenges/{challengeId}/code-versions`

**Descripción:** Agrega una versión de código con el lenguaje de programación y plantilla inicial.

**Request:**

```http
POST http://localhost:8082/api/v1/challenges/{challengeId}/code-versions
Content-Type: application/json

{
  "language": "PYTHON",
  "defaultCode": "def sum_two_numbers(a, b):\n    # Tu código aquí\n    pass\n\nif __name__ == '__main__':\n    a = int(input())\n    b = int(input())\n    print(sum_two_numbers(a, b))"
}
```

**Ejemplo con lenguaje real:**

```http
POST http://localhost:8082/api/v1/challenges/a1b2c3d4-e5f6-7890-abcd-ef1234567890/code-versions
Content-Type: application/json

{
  "language": "PYTHON",
  "defaultCode": "def sum_two_numbers(a, b):\n    # Tu código aquí\n    pass\n\nif __name__ == '__main__':\n    a = int(input())\n    b = int(input())\n    print(sum_two_numbers(a, b))"
}
```

**Response Esperada:**

```json
{
  "id": "b2c3d4e5-f6a7-8901-bcde-f12345678901",
  "challengeId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "language": "PYTHON",
  "defaultCode": "def sum_two_numbers(a, b):\n    # Tu código aquí\n    pass\n\nif __name__ == '__main__':\n    a = int(input())\n    b = int(input())\n    print(sum_two_numbers(a, b))",
  "createdAt": "2025-10-18T10:01:00"
}
```

> ⚠️ **IMPORTANTE:** Copia el `id` del response y úsalo como `{codeVersionId}` en los siguientes pasos.

---

### **PASO 3: Agregar Tests a la Code Version**

**Endpoint:** `POST /api/v1/challenges/{challengeId}/code-versions/{codeVersionId}/tests`

**Descripción:** Agrega tests de validación que se ejecutarán contra la solución del estudiante.

#### Test 1:

```http
POST http://localhost:8082/api/v1/challenges/{challengeId}/code-versions/{codeVersionId}/tests
Content-Type: application/json

{
  "input": "2\n3",
  "expectedOutput": "5",
  "failureMessage": "La suma de 2 + 3 debe ser 5"
}
```

#### Test 2:

```http
POST http://localhost:8082/api/v1/challenges/{challengeId}/code-versions/{codeVersionId}/tests
Content-Type: application/json

{
  "input": "10\n20",
  "expectedOutput": "30",
  "failureMessage": "La suma de 10 + 20 debe ser 30"
}
```

#### Test 3:

```http
POST http://localhost:8082/api/v1/challenges/{challengeId}/code-versions/{codeVersionId}/tests
Content-Type: application/json

{
  "input": "-5\n5",
  "expectedOutput": "0",
  "failureMessage": "La suma de -5 + 5 debe ser 0"
}
```

#### Test 4:

```http
POST http://localhost:8082/api/v1/challenges/{challengeId}/code-versions/{codeVersionId}/tests
Content-Type: application/json

{
  "input": "100\n200",
  "expectedOutput": "300",
  "failureMessage": "La suma de 100 + 200 debe ser 300"
}
```

**Response Esperada (para cada test):**

```json
{
  "id": "c3d4e5f6-a7b8-9012-cdef-123456789012",
  "codeVersionId": "b2c3d4e5-f6a7-8901-bcde-f12345678901",
  "input": "2\n3",
  "expectedOutput": "5",
  "customValidationCode": null,
  "failureMessage": "La suma de 2 + 3 debe ser 5"
}
```

---

### **PASO 4: Publicar el Challenge**

**Endpoint:** `PATCH /api/v1/challenges/{challengeId}`

**Descripción:** Cambia el estado del challenge a `PUBLISHED` para que esté disponible para los estudiantes.

**Request:**

```http
PATCH http://localhost:8082/api/v1/challenges/{challengeId}
Content-Type: application/json

{
  "status": "PUBLISHED"
}
```

**Response Esperada:**

```json
{
  "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "teacherId": "550e8400-e29b-41d4-a716-446655440000",
  "name": "Suma de Dos Números",
  "description": "Implementa una función que sume dos números enteros",
  "experiencePoints": 50,
  "difficulty": "EASY",
  "status": "PUBLISHED",
  "createdAt": "2025-10-18T10:00:00",
  "publishedAt": "2025-10-18T10:05:00"
}
```

---

### **PASO 5: Verificar Challenges Publicados**

**Endpoint:** `GET /api/v1/challenges`

**Descripción:** Lista todos los challenges publicados disponibles para estudiantes.

**Request:**

```http
GET http://localhost:8082/api/v1/challenges
```

**Response Esperada:**

```json
[
  {
    "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
    "teacherId": "550e8400-e29b-41d4-a716-446655440000",
    "name": "Suma de Dos Números",
    "description": "Implementa una función que sume dos números enteros",
    "experiencePoints": 50,
    "difficulty": "EASY",
    "status": "PUBLISHED",
    "createdAt": "2025-10-18T10:00:00",
    "publishedAt": "2025-10-18T10:05:00"
  }
]
```

---

### **PASO 6: Crear una Solución (Estudiante)**

**Endpoint:** `POST /api/v1/solutions`

**Descripción:** El estudiante crea una solución inicial para el challenge.

**Request:**

```http
POST http://localhost:8082/api/v1/solutions
Content-Type: application/json

{
  "challengeId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "codeVersionId": "b2c3d4e5-f6a7-8901-bcde-f12345678901",
  "studentId": "660e8400-e29b-41d4-a716-446655440001",
  "code": "def sum_two_numbers(a, b):\n    # Tu código aquí\n    pass\n\nif __name__ == '__main__':\n    a = int(input())\n    b = int(input())\n    print(sum_two_numbers(a, b))"
}
```

**Response Esperada:**

```json
{
  "id": "d4e5f6a7-b8c9-0123-def0-123456789abc",
  "challengeId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "codeVersionId": "b2c3d4e5-f6a7-8901-bcde-f12345678901",
  "studentId": "660e8400-e29b-41d4-a716-446655440001",
  "attempts": 0,
  "code": "def sum_two_numbers(a, b):\n    # Tu código aquí\n    pass\n\nif __name__ == '__main__':\n    a = int(input())\n    b = int(input())\n    print(sum_two_numbers(a, b))",
  "lastAttemptAt": null,
  "status": "IN_PROGRESS",
  "pointsEarned": 0,
  "maxPoints": 0,
  "successPercentage": 0.0
}
```

> ⚠️ **IMPORTANTE:** Copia el `id` del response y úsalo como `{solutionId}` en los siguientes pasos.

---

### **PASO 7: Actualizar la Solución (Estudiante escribe código)**

**Endpoint:** `PUT /api/v1/solutions/{solutionId}`

**Descripción:** El estudiante actualiza su código antes de hacer submit.

**Request:**

```http
PUT http://localhost:8082/api/v1/solutions/{solutionId}
Content-Type: application/json

{
  "code": "def sum_two_numbers(a, b):\n    return a + b\n\nif __name__ == '__main__':\n    a = int(input())\n    b = int(input())\n    print(sum_two_numbers(a, b))",
  "language": "PYTHON"
}
```

**Response Esperada:**

```json
{
  "id": "d4e5f6a7-b8c9-0123-def0-123456789abc",
  "challengeId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "codeVersionId": "b2c3d4e5-f6a7-8901-bcde-f12345678901",
  "studentId": "660e8400-e29b-41d4-a716-446655440001",
  "attempts": 0,
  "code": "def sum_two_numbers(a, b):\n    return a + b\n\nif __name__ == '__main__':\n    a = int(input())\n    b = int(input())\n    print(sum_two_numbers(a, b))",
  "lastAttemptAt": null,
  "status": "IN_PROGRESS",
  "pointsEarned": 0,
  "maxPoints": 0,
  "successPercentage": 0.0
}
```

---

### **PASO 8: Submit de la Solución ⭐ (ENDPOINT CLAVE)**

**Endpoint:** `POST /api/v1/solutions/{solutionId}/submit`

**Descripción:** Envía la solución para evaluación. Este endpoint:
1. Envía el código al CodeRunner vía gRPC
2. Ejecuta los tests
3. Calcula los puntos ganados
4. **Publica evento `ChallengeCompletedEvent` a Kafka**
5. Profile Service consume el evento y actualiza el score del estudiante

**Request:**

```http
POST http://localhost:8082/api/v1/solutions/{solutionId}/submit
Content-Type: application/json

{
  "studentId": "660e8400-e29b-41d4-a716-446655440001",
  "code": "def sum_two_numbers(a, b):\n    return a + b\n\nif __name__ == '__main__':\n    a = int(input())\n    b = int(input())\n    print(sum_two_numbers(a, b))"
}
```

**Response Esperada (Si todos los tests pasan):**

```json
{
  "solutionReportId": "e5f6a7b8-c9d0-1234-ef01-23456789abcd",
  "message": "Solution executed via CodeRunner. All tests passed. 4 out of 4 tests passed (100.0%). Score: 50/50 points. Execution time: 123 ms",
  "success": true,
  "approvedTestIds": [
    "c3d4e5f6-a7b8-9012-cdef-123456789012",
    "c3d4e5f6-a7b8-9012-cdef-123456789013",
    "c3d4e5f6-a7b8-9012-cdef-123456789014",
    "c3d4e5f6-a7b8-9012-cdef-123456789015"
  ],
  "totalTests": 4,
  "passedTests": 4,
  "executionDetails": "Execution completed in 123 ms. Score: 50/50 points",
  "timeTaken": 123.0
}
```

**Response Esperada (Si algunos tests fallan):**

```json
{
  "solutionReportId": "e5f6a7b8-c9d0-1234-ef01-23456789abcd",
  "message": "Solution executed via CodeRunner. Some tests failed. 2 out of 4 tests passed (50.0%). Score: 0/50 points. Execution time: 95 ms",
  "success": false,
  "approvedTestIds": [
    "c3d4e5f6-a7b8-9012-cdef-123456789012",
    "c3d4e5f6-a7b8-9012-cdef-123456789013"
  ],
  "totalTests": 4,
  "passedTests": 2,
  "executionDetails": "Execution completed in 95 ms. Score: 0/50 points",
  "timeTaken": 95.0
}
```

#### 📤 Evento Kafka Publicado

**Condición:** Solo se publica si `pointsEarned > 0` (todos los tests pasan con la estrategia actual)

**Tópico:** `challenge-completed` (configurado en `application.properties`)

**Payload del Evento:**

```json
{
  "studentId": "660e8400-e29b-41d4-a716-446655440001",
  "challengeId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "solutionId": "d4e5f6a7-b8c9-0123-def0-123456789abc",
  "experiencePointsEarned": 50,
  "totalExperiencePoints": 50,
  "passedTests": 4,
  "totalTests": 4,
  "allTestsPassed": true,
  "executionTimeMs": 123,
  "completedAt": "2025-10-18T10:30:00",
  "occurredOn": "2025-10-18T10:30:00"
}
```

**Código Fuente:**
- Controlador: [`SolutionController.java:121-163`](../src/main/java/com/levelupjourney/microservicechallenges/solutions/interfaces/rest/SolutionController.java#L121-L163)
- Servicio: [`SolutionCommandServiceImpl.java:54-227`](../src/main/java/com/levelupjourney/microservicechallenges/solutions/application/internal/commandservices/SolutionCommandServiceImpl.java#L54-L227)
- Publicación Kafka: [`SolutionCommandServiceImpl.java:173-192`](../src/main/java/com/levelupjourney/microservicechallenges/solutions/application/internal/commandservices/SolutionCommandServiceImpl.java#L173-L192)
- Kafka Producer: [`KafkaProducerService.java:37-62`](../src/main/java/com/levelupjourney/microservicechallenges/shared/infrastructure/messaging/kafka/KafkaProducerService.java#L37-L62)

---

### **PASO 9: Verificar la Solución con Score Actualizado**

**Endpoint:** `GET /api/v1/solutions/{solutionId}`

**Descripción:** Obtiene la solución con el score asignado después del submit.

**Request:**

```http
GET http://localhost:8082/api/v1/solutions/{solutionId}
```

**Response Esperada:**

```json
{
  "id": "d4e5f6a7-b8c9-0123-def0-123456789abc",
  "challengeId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "codeVersionId": "b2c3d4e5-f6a7-8901-bcde-f12345678901",
  "studentId": "660e8400-e29b-41d4-a716-446655440001",
  "attempts": 1,
  "code": "def sum_two_numbers(a, b):\n    return a + b\n\nif __name__ == '__main__':\n    a = int(input())\n    b = int(input())\n    print(sum_two_numbers(a, b))",
  "lastAttemptAt": "2025-10-18T10:30:00",
  "status": "SUBMITTED",
  "pointsEarned": 50,
  "maxPoints": 50,
  "successPercentage": 100.0
}
```

---

## ✅ Verificación del Flujo

### 1. Verificar Logs del Microservicio Challenges

Busca en los logs estas líneas clave:

```
🎯 =============== SUBMIT SOLUTION PROCESS STARTED ===============
📋 Step 5: Fetching challenge details for score calculation...
💯 Score calculated:
  - Points Earned: 50/50
📤 Publishing ChallengeCompletedEvent to Kafka...
✅ Event published successfully
🎯 =============== SUBMIT SOLUTION PROCESS COMPLETED ===============
```

### 2. Verificar Kafka

Usa un cliente de Kafka para verificar que el evento fue publicado:

```bash
# Consumir mensajes del tópico
kafka-console-consumer --bootstrap-server localhost:9092 \
  --topic challenge-completed \
  --from-beginning
```

### 3. Verificar Profile Service

Verifica en el microservicio de Profiles que el score del estudiante fue actualizado:

```http
GET http://localhost:8083/api/v1/profiles/{studentId}
```

---

## 🔍 Endpoints Adicionales Útiles

### Obtener Solution por Student y CodeVersion

```http
GET http://localhost:8082/api/v1/solutions/students/{studentId}/code-versions/{codeVersionId}
```

### Obtener Solution por Challenge, CodeVersion y Student

```http
GET http://localhost:8082/api/v1/solutions/challenges/{challengeId}/code-versions/{codeVersionId}/students/{studentId}
```

### Listar Code Versions de un Challenge

```http
GET http://localhost:8082/api/v1/challenges/{challengeId}/code-versions
```

### Listar Tests de una Code Version

```http
GET http://localhost:8082/api/v1/challenges/{challengeId}/code-versions/{codeVersionId}/tests
```

### Obtener Challenges por Teacher ID

```http
GET http://localhost:8082/api/v1/challenges/teachers/{teacherId}
```

---

## 🐛 Troubleshooting

### Problema 1: No se publican eventos a Kafka

**Síntomas:** El submit funciona pero no se publica ningún evento.

**Causas posibles:**
1. `pointsEarned = 0` (no todos los tests pasaron)
2. Kafka no está corriendo
3. Configuración incorrecta del tópico

**Solución:**
```bash
# Verifica que todos los tests pasen
# Revisa los logs para ver el score calculado
# Verifica la configuración de Kafka en application.properties
```

### Problema 2: Error al ejecutar el código

**Síntomas:** Error 500 al hacer submit.

**Causas posibles:**
1. CodeRunner no está corriendo
2. Error de conexión gRPC
3. Tests mal configurados

**Solución:**
```bash
# Verifica que CodeRunner esté corriendo
# Revisa los logs del microservicio
# Verifica la configuración gRPC
```

### Problema 3: Puntos siempre en 0

**Síntomas:** El código pasa algunos tests pero los puntos son 0.

**Causa:** La estrategia de puntuación actual es "todo o nada" (all-or-nothing).

**Solución:** Para habilitar puntos proporcionales, edita [`SolutionCommandServiceImpl.java:271`](../src/main/java/com/levelupjourney/microservicechallenges/solutions/application/internal/commandservices/SolutionCommandServiceImpl.java#L271):

```java
// Descomenta esta línea para puntos proporcionales:
return (maxPoints * passedTests) / totalTests;
```

---

## 📊 Estrategia de Puntuación

### Estrategia Actual: All-or-Nothing

- ✅ **Todos los tests pasan:** `pointsEarned = experiencePoints` (50/50)
- ❌ **Algún test falla:** `pointsEarned = 0` (0/50)

**Código:** [`SolutionCommandServiceImpl.java:255-275`](../src/main/java/com/levelupjourney/microservicechallenges/solutions/application/internal/commandservices/SolutionCommandServiceImpl.java#L255-L275)

```java
if (allPassed) {
    return maxPoints;
}
return 0; // No points if not all tests pass
```

### Estrategia Alternativa: Proporcional

Para habilitar puntos proporcionales:

```java
if (allPassed) {
    return maxPoints;
}
// Uncomment for proportional scoring:
return (maxPoints * passedTests) / totalTests;
```

**Ejemplo:**
- 2 de 4 tests pasan → `pointsEarned = 25` (50 * 2 / 4)

---

## 📚 Referencias

- **Código Fuente del Flujo:**
  - Controlador de Solutions: [`SolutionController.java`](../src/main/java/com/levelupjourney/microservicechallenges/solutions/interfaces/rest/SolutionController.java)
  - Servicio de Commands: [`SolutionCommandServiceImpl.java`](../src/main/java/com/levelupjourney/microservicechallenges/solutions/application/internal/commandservices/SolutionCommandServiceImpl.java)
  - Evento de Dominio: [`ChallengeCompletedEvent.java`](../src/main/java/com/levelupjourney/microservicechallenges/solutions/domain/model/events/ChallengeCompletedEvent.java)
  - Productor Kafka: [`KafkaProducerService.java`](../src/main/java/com/levelupjourney/microservicechallenges/shared/infrastructure/messaging/kafka/KafkaProducerService.java)

- **Archivo de Tests HTTP:**
  - [`complete-flow-endpoints.http`](./complete-flow-endpoints.http)

---

## 🎯 Resumen del Flujo de Puntos

```
POST /api/v1/solutions/{solutionId}/submit
  ↓
SolutionController.submitSolution()
  ↓
SolutionCommandServiceImpl.handle(SubmitSolutionCommand)
  ↓
CodeRunnerExecutionService.executeSolution() [gRPC]
  ↓
calculateScore(maxPoints, passedTests, totalTests, allPassed)
  ↓
solution.assignScore(pointsEarned, maxPoints)
  ↓
if (pointsEarned > 0):
    new ChallengeCompletedEvent(...)
    ↓
    KafkaProducerService.publishChallengeCompleted(event)
    ↓
    Kafka Topic: "challenge-completed"
    ↓
    Profile Service (Consumer)
    ↓
    Actualiza score del estudiante
```

---

**Última actualización:** 2025-10-18
**Versión del microservicio:** 1.0.0
