# 🚀 Flujo Completo - Challenge "Hola Mundo" en C++

Esta guía te permitirá probar el flujo completo end-to-end usando un challenge simple de "Hola Mundo" en C++.

---

## 📋 Prerequisitos

- ✅ Microservicio Challenges corriendo en `http://localhost:8083`
- ✅ Microservicio CodeRunner corriendo (gRPC)
- ✅ Kafka corriendo
- ✅ Microservicio Profiles corriendo y escuchando eventos Kafka

---

## 🎯 PASO 1: Crear el Challenge

```http
POST http://localhost:8083/api/v1/challenges
Content-Type: application/json

{
  "teacherId": "550e8400-e29b-41d4-a716-446655440000",
  "name": "Hola Mundo en C++",
  "description": "Escribe un programa que imprima 'Hola Mundo' en la consola",
  "experiencePoints": 100,
  "difficulty": "EASY"
}
```

**Response Esperada:**
```json
{
  "id": "GUARDA_ESTE_CHALLENGE_ID",
  "teacherId": "550e8400-e29b-41d4-a716-446655440000",
  "name": "Hola Mundo en C++",
  "description": "Escribe un programa que imprima 'Hola Mundo' en la consola",
  "experiencePoints": 100,
  "difficulty": "EASY",
  "status": "DRAFT",
  "tags": [],
  "stars": []
}
```

> ⚠️ **IMPORTANTE:** Copia el `id` y úsalo como `{CHALLENGE_ID}` en los siguientes pasos.

---

## 🎯 PASO 2: Agregar Code Version (C++)

```http
POST http://localhost:8083/api/v1/challenges/{CHALLENGE_ID}/code-versions
Content-Type: application/json

{
  "language": "C_PLUS_PLUS",
  "defaultCode": "#include <iostream>\nusing namespace std;\n\nint main() {\n    // Escribe tu código aquí\n    return 0;\n}"
}
```

**Response Esperada:**
```json
{
  "id": "GUARDA_ESTE_CODE_VERSION_ID",
  "challengeId": "{CHALLENGE_ID}",
  "language": "C_PLUS_PLUS",
  "initialCode": "#include <iostream>\nusing namespace std;\n\nint main() {\n    // Escribe tu código aquí\n    return 0;\n}"
}
```

> ⚠️ **IMPORTANTE:** Copia el `id` y úsalo como `{CODE_VERSION_ID}` en los siguientes pasos.

---

## 🎯 PASO 3: Agregar Tests (Mínimo 3)

### Test 1: Hola Mundo básico

```http
POST http://localhost:8083/api/v1/challenges/{CHALLENGE_ID}/code-versions/{CODE_VERSION_ID}/tests
Content-Type: application/json

{
  "input": "",
  "expectedOutput": "Hola Mundo",
  "failureMessage": "El programa debe imprimir 'Hola Mundo'"
}
```

### Test 2: Hola Mundo con salto de línea

```http
POST http://localhost:8083/api/v1/challenges/{CHALLENGE_ID}/code-versions/{CODE_VERSION_ID}/tests
Content-Type: application/json

{
  "input": "",
  "expectedOutput": "Hola Mundo\n",
  "failureMessage": "El programa debe imprimir 'Hola Mundo' con salto de línea"
}
```

### Test 3: Verificación de salida exacta

```http
POST http://localhost:8083/api/v1/challenges/{CHALLENGE_ID}/code-versions/{CODE_VERSION_ID}/tests
Content-Type: application/json

{
  "input": "",
  "expectedOutput": "Hola Mundo",
  "failureMessage": "La salida debe ser exactamente 'Hola Mundo'"
}
```

**Response Esperada (para cada test):**
```json
{
  "id": "uuid-del-test",
  "codeVersionId": "{CODE_VERSION_ID}",
  "input": "",
  "expectedOutput": "Hola Mundo",
  "customValidationCode": null,
  "failureMessage": "El programa debe imprimir 'Hola Mundo'"
}
```

---

## 🎯 PASO 4: Verificar que tienes 3 tests

```http
GET http://localhost:8083/api/v1/challenges/{CHALLENGE_ID}/code-versions/{CODE_VERSION_ID}/tests
```

**Response Esperada:**
```json
[
  {
    "id": "test-1-id",
    "codeVersionId": "{CODE_VERSION_ID}",
    "input": "",
    "expectedOutput": "Hola Mundo",
    "customValidationCode": null,
    "failureMessage": "El programa debe imprimir 'Hola Mundo'"
  },
  {
    "id": "test-2-id",
    "codeVersionId": "{CODE_VERSION_ID}",
    "input": "",
    "expectedOutput": "Hola Mundo\n",
    "customValidationCode": null,
    "failureMessage": "El programa debe imprimir 'Hola Mundo' con salto de línea"
  },
  {
    "id": "test-3-id",
    "codeVersionId": "{CODE_VERSION_ID}",
    "input": "",
    "expectedOutput": "Hola Mundo",
    "customValidationCode": null,
    "failureMessage": "La salida debe ser exactamente 'Hola Mundo'"
  }
]
```

✅ Debes tener **mínimo 3 tests** para poder publicar el challenge.

---

## 🎯 PASO 5: Publicar el Challenge

```http
PATCH http://localhost:8083/api/v1/challenges/{CHALLENGE_ID}
Content-Type: application/json

{
  "status": "PUBLISHED"
}
```

**Response Esperada:**
```json
{
  "id": "{CHALLENGE_ID}",
  "teacherId": "550e8400-e29b-41d4-a716-446655440000",
  "name": "Hola Mundo en C++",
  "description": "Escribe un programa que imprima 'Hola Mundo' en la consola",
  "experiencePoints": 100,
  "difficulty": "EASY",
  "status": "PUBLISHED",
  "tags": [],
  "stars": []
}
```

✅ El challenge ahora está **PUBLISHED** y disponible para estudiantes.

---

## 🎯 PASO 6: Crear una Solución (Estudiante)

El estudiante crea una solución inicial:

```http
POST http://localhost:8083/api/v1/solutions
Content-Type: application/json

{
  "challengeId": "{CHALLENGE_ID}",
  "codeVersionId": "{CODE_VERSION_ID}",
  "studentId": "660e8400-e29b-41d4-a716-446655440001",
  "code": "#include <iostream>\nusing namespace std;\n\nint main() {\n    // Escribe tu código aquí\n    return 0;\n}"
}
```

**Response Esperada:**
```json
{
  "id": "GUARDA_ESTE_SOLUTION_ID",
  "challengeId": "{CHALLENGE_ID}",
  "codeVersionId": "{CODE_VERSION_ID}",
  "studentId": "660e8400-e29b-41d4-a716-446655440001",
  "attempts": 0,
  "code": "#include <iostream>\nusing namespace std;\n\nint main() {\n    // Escribe tu código aquí\n    return 0;\n}",
  "lastAttemptAt": null,
  "status": "IN_PROGRESS",
  "pointsEarned": 0,
  "maxPoints": 0,
  "successPercentage": 0.0
}
```

> ⚠️ **IMPORTANTE:** Copia el `id` y úsalo como `{SOLUTION_ID}` en los siguientes pasos.

---

## 🎯 PASO 7: Actualizar la Solución (Estudiante escribe código)

El estudiante actualiza su código con la solución correcta:

```http
PUT http://localhost:8083/api/v1/solutions/{SOLUTION_ID}
Content-Type: application/json

{
  "code": "#include <iostream>\nusing namespace std;\n\nint main() {\n    cout << \"Hola Mundo\";\n    return 0;\n}",
  "language": "C_PLUS_PLUS"
}
```

**Response Esperada:**
```json
{
  "id": "{SOLUTION_ID}",
  "challengeId": "{CHALLENGE_ID}",
  "codeVersionId": "{CODE_VERSION_ID}",
  "studentId": "660e8400-e29b-41d4-a716-446655440001",
  "attempts": 0,
  "code": "#include <iostream>\nusing namespace std;\n\nint main() {\n    cout << \"Hola Mundo\";\n    return 0;\n}",
  "lastAttemptAt": null,
  "status": "IN_PROGRESS",
  "pointsEarned": 0,
  "maxPoints": 0,
  "successPercentage": 0.0
}
```

---

## 🎯 PASO 8: ⭐ Submit de la Solución (ENDPOINT CLAVE)

Este es el paso más importante que:
1. Ejecuta el código en CodeRunner (gRPC)
2. Valida con los tests
3. Calcula los puntos
4. **Publica evento `ChallengeCompletedEvent` a Kafka**
5. Profile Service recibe el evento y actualiza el score

```http
POST http://localhost:8083/api/v1/solutions/{SOLUTION_ID}/submit
Content-Type: application/json

{
  "studentId": "660e8400-e29b-41d4-a716-446655440001",
  "code": "#include <iostream>\nusing namespace std;\n\nint main() {\n    cout << \"Hola Mundo\";\n    return 0;\n}"
}
```

**Response Esperada (Si todos los tests pasan):**
```json
{
  "solutionReportId": "uuid-del-report",
  "message": "Solution executed via CodeRunner. All tests passed. 3 out of 3 tests passed (100.0%). Score: 100/100 points. Execution time: 245 ms",
  "success": true,
  "approvedTestIds": [
    "test-1-id",
    "test-2-id",
    "test-3-id"
  ],
  "totalTests": 3,
  "passedTests": 3,
  "executionDetails": "Execution completed in 245 ms. Score: 100/100 points",
  "timeTaken": 245.0
}
```

### 📤 Evento Kafka Publicado

**Tópico:** `challenge-completed`

**Payload:**
```json
{
  "studentId": "660e8400-e29b-41d4-a716-446655440001",
  "challengeId": "{CHALLENGE_ID}",
  "solutionId": "{SOLUTION_ID}",
  "experiencePointsEarned": 100,
  "totalExperiencePoints": 100,
  "passedTests": 3,
  "totalTests": 3,
  "allTestsPassed": true,
  "executionTimeMs": 245,
  "completedAt": "2025-10-18T10:30:00",
  "occurredOn": "2025-10-18T10:30:00"
}
```

---

## 🎯 PASO 9: Verificar la Solución con Score Actualizado

```http
GET http://localhost:8083/api/v1/solutions/{SOLUTION_ID}
```

**Response Esperada:**
```json
{
  "id": "{SOLUTION_ID}",
  "challengeId": "{CHALLENGE_ID}",
  "codeVersionId": "{CODE_VERSION_ID}",
  "studentId": "660e8400-e29b-41d4-a716-446655440001",
  "attempts": 1,
  "code": "#include <iostream>\nusing namespace std;\n\nint main() {\n    cout << \"Hola Mundo\";\n    return 0;\n}",
  "lastAttemptAt": "2025-10-18T10:30:00",
  "status": "SUBMITTED",
  "pointsEarned": 100,
  "maxPoints": 100,
  "successPercentage": 100.0
}
```

✅ Los puntos se han calculado y guardado!

---

## 📊 Verificación del Flujo Completo

### 1️⃣ Logs del Microservicio Challenges

Busca estos mensajes en los logs:

```
🎯 =============== SUBMIT SOLUTION PROCESS STARTED ===============
📋 Submit Solution Command received:
  - Solution ID: '{SOLUTION_ID}'
  - Student ID: '660e8400-e29b-41d4-a716-446655440001'

🔍 Step 1: Validating solution exists...
✅ Solution found

🔍 Step 2: Fetching CodeVersion details from external service...
✅ CodeVersion details retrieved:
  - Programming Language: 'C_PLUS_PLUS'
  - Total Tests: 3

🚀 Step 3: Submitting to CodeRunner via gRPC...

🎉 Step 4: Processing execution results...
📊 Final execution summary:
  - Completed: true
  - Total Tests: 3
  - Passed Tests: 3
  - Success Rate: 100.0%
  - Execution Time: 245 ms

📋 Step 5: Fetching challenge details for score calculation...
✅ Challenge details retrieved:
  - Challenge ID: '{CHALLENGE_ID}'
  - Max Experience Points: 100

💯 Score calculated:
  - Points Earned: 100/100
  - Success Rate: 100.0%

✅ Score saved to solution

📤 Publishing ChallengeCompletedEvent to Kafka...
  - Topic: 'challenge-completed'
  - Student ID: '660e8400-e29b-41d4-a716-446655440001'
  - Points Earned: 100/100
  - Tests Passed: 3/3

✅ Event published successfully
  - Partition: 0
  - Offset: 123

🎯 =============== SUBMIT SOLUTION PROCESS COMPLETED ===============
```

### 2️⃣ Verificar Kafka

Consume mensajes del tópico para ver el evento:

```bash
kafka-console-consumer --bootstrap-server localhost:9092 \
  --topic challenge-completed \
  --from-beginning
```

### 3️⃣ Verificar Profile Service

Consulta el score del estudiante en el microservicio de Profiles:

```http
GET http://localhost:8084/api/v1/profiles/660e8400-e29b-41d4-a716-446655440001
```

Deberías ver que el score se incrementó en **100 puntos**.

---

## 📝 Variaciones del Código C++

### ✅ Código Correcto (100 puntos)

```cpp
#include <iostream>
using namespace std;

int main() {
    cout << "Hola Mundo";
    return 0;
}
```

### ❌ Código Incorrecto (0 puntos)

```cpp
#include <iostream>
using namespace std;

int main() {
    cout << "Hello World";  // ❌ Texto incorrecto
    return 0;
}
```

### ❌ Código con Error de Compilación (0 puntos)

```cpp
#include <iostream>
using namespace std;

int main() {
    cout << "Hola Mundo"  // ❌ Falta punto y coma
    return 0;
}
```

---

## 🎯 Resumen del Flujo

```
1. POST /api/v1/challenges
   → Crear Challenge

2. POST /api/v1/challenges/{id}/code-versions
   → Agregar Code Version (C++)

3. POST /api/v1/challenges/{id}/code-versions/{id}/tests (x3)
   → Agregar Tests

4. PATCH /api/v1/challenges/{id}
   → Publicar Challenge

5. POST /api/v1/solutions
   → Estudiante crea solución

6. PUT /api/v1/solutions/{id}
   → Estudiante actualiza código

7. POST /api/v1/solutions/{id}/submit ⭐
   → Submit para evaluación
   ↓
   CodeRunner (gRPC) → Ejecuta tests
   ↓
   Calcula puntos
   ↓
   Kafka: ChallengeCompletedEvent
   ↓
   Profile Service → Actualiza score
```

---

## 🔧 Troubleshooting

### Problema: Error 400 al publicar

**Causa:** No tienes al menos 3 tests.

**Solución:** Verifica con `GET /api/v1/challenges/{id}/code-versions/{id}/tests` que tengas mínimo 3 tests.

---

### Problema: No se publica evento a Kafka

**Causa:** `pointsEarned = 0` (no todos los tests pasaron).

**Solución:** Asegúrate de que el código sea correcto y pase todos los tests. La estrategia actual es "all-or-nothing".

---

### Problema: Error al ejecutar código

**Causa:** CodeRunner no está corriendo o hay error de conexión gRPC.

**Solución:** Verifica que CodeRunner esté corriendo y accesible.

---

## 📚 Archivos de Código Fuente

- **Submit Controller:** `SolutionController.java:121-163`
- **Submit Service:** `SolutionCommandServiceImpl.java:54-227`
- **Kafka Producer:** `KafkaProducerService.java:37-62`
- **Evento:** `ChallengeCompletedEvent.java`

---

## ✅ Checklist Final

Antes de hacer submit, asegúrate de que:

- ✅ Challenge está en estado `PUBLISHED`
- ✅ Code Version tiene lenguaje `C_PLUS_PLUS`
- ✅ Hay al menos 3 tests creados
- ✅ Solution está creada con el `studentId` correcto
- ✅ El código está actualizado con la solución correcta
- ✅ CodeRunner está corriendo
- ✅ Kafka está corriendo
- ✅ Profile Service está escuchando Kafka

---

**Última actualización:** 2025-10-18
**Lenguaje:** C++
**Puntos:** 100 XP
