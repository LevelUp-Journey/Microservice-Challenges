# Documentación de Endpoints de la API de Microservice-Challenges

Esta documentación detalla cada endpoint utilizado en el flujo de creación, publicación y resolución de challenges. Cada endpoint se describe de manera extremadamente específica, incluyendo método HTTP, URL, headers, body de solicitud, respuesta esperada y notas adicionales.

## 1. Crear un Nuevo Challenge

### Método HTTP
`POST`

### URL
`http://localhost:8082/api/v1/challenges`

### Headers
- `Content-Type: application/json`

### Body de Solicitud (JSON)
```json
{
  "teacherId": "550e8400-e29b-41d4-a716-446655440000",
  "name": "Challenge de Fibonacci en C++",
  "description": "Implementa una función que calcule el número de Fibonacci en una posición dada en C++",
  "experiencePoints": 100
}
```

#### Descripción de Campos:
- `teacherId` (string, requerido): UUID del profesor que crea el challenge. Debe ser un identificador único válido.
- `name` (string, requerido): Nombre del challenge. Máximo 255 caracteres.
- `description` (string, requerido): Descripción detallada del challenge. Máximo 1000 caracteres.
- `experiencePoints` (integer, requerido): Puntos de experiencia otorgados al completar el challenge. Debe ser un número positivo.

### Respuesta Esperada
- **Código de Estado**: `201 Created`
- **Body**: Objeto JSON con los detalles del challenge creado, incluyendo un campo `id` (UUID) que debe usarse en pasos posteriores.

### Notas
- Este endpoint inicia el proceso de creación de un challenge.
- El challenge se crea en estado "DRAFT" inicialmente.

## 2. Agregar una Code Version al Challenge

### Método HTTP
`POST`

### URL
`http://localhost:8082/api/v1/code-versions`

### Headers
- `Content-Type: application/json`

### Body de Solicitud (JSON)
```json
{
  "challengeId": "CHALLENGE_ID_ANTERIOR",
  "language": "C_PLUS_PLUS",
  "defaultCode": "#include <iostream>\n\nint fibonacci(int position) {\n    // Implementa la lógica aquí\n    return 0;\n}\n\nint main() {\n    int pos;\n    std::cin >> pos;\n    std::cout << fibonacci(pos) << std::endl;\n    return 0;\n}"
}
```

#### Descripción de Campos:
- `challengeId` (string, requerido): UUID del challenge al que se asocia esta code version. Debe coincidir con el ID generado en el paso anterior.
- `language` (string, requerido): Lenguaje de programación. Valores permitidos: "C_PLUS_PLUS", "JAVA", "PYTHON", etc.
- `defaultCode` (string, requerido): Código base o plantilla que se proporciona a los estudiantes. Incluye headers, namespaces y una función skeleton. Debe ser un string válido de código fuente.

### Respuesta Esperada
- **Código de Estado**: `201 Created`
- **Body**: Objeto JSON con los detalles de la code version creada, incluyendo un campo `id` (UUID) que debe usarse en pasos posteriores.

### Notas
- Una code version define el lenguaje y el código inicial para un challenge.
- El `defaultCode` debe incluir una función específica (ej. `fibonacci(int position)`) que el estudiante debe implementar.

## 3. Agregar un Test a la Code Version

### Método HTTP
`POST`

### URL
`http://localhost:8082/api/v1/code-version-tests`

### Headers
- `Content-Type: application/json`

### Body de Solicitud (JSON) - Ejemplo para primer test
```json
{
  "codeVersionId": "CODE_VERSION_ID_ANTERIOR",
  "input": "4",
  "expectedOutput": "3",
  "failureMessage": "El número de Fibonacci en la posición 4 debe ser 3"
}
```

#### Descripción de Campos:
- `codeVersionId` (string, requerido): UUID de la code version a la que pertenece el test.
- `input` (string, requerido): Entrada que se pasará al programa del estudiante. Para Fibonacci, es la posición (ej. "4").
- `expectedOutput` (string, requerido): Salida esperada del programa. Para Fibonacci, es el número calculado (ej. "3").
- `failureMessage` (string, opcional): Mensaje personalizado mostrado si el test falla. Si no se proporciona, se usa un mensaje genérico.
- `customValidationCode` (string, opcional): Código personalizado para validación avanzada usando frameworks como doctest. Solo se usa cuando el teacher requiere lógica de test específica.

### Respuesta Esperada
- **Código de Estado**: `201 Created`
- **Body**: Objeto JSON con los detalles del test creado.

### Notas
- Este endpoint se llama múltiples veces para agregar varios tests.
- `customValidationCode` es opcional y raro; normalmente solo se usan `input` y `expectedOutput`.
- Para Fibonacci: Test 1: input "4" → expected "3"; Test 2: input "10" → expected "55"; Test 3: input "15" → expected "610".

## 4. Publicar el Challenge

### Método HTTP
`PATCH`

### URL
`http://localhost:8082/api/v1/challenges/{challengeId}/publish`

### Headers
- Ninguno requerido (puede incluir `Content-Type: application/json` si se envía body vacío).

### Body de Solicitud
Vacío (o `{}` si se requiere JSON).

### Parámetros de URL:
- `challengeId` (string, requerido): UUID del challenge a publicar.

### Respuesta Esperada
- **Código de Estado**: `200 OK`
- **Body**: Objeto JSON con el challenge actualizado, incluyendo `status: "PUBLISHED"`.

### Notas
- Cambia el estado del challenge de "DRAFT" a "PUBLISHED", haciéndolo visible para estudiantes.

## 5. Iniciar el Challenge para un Estudiante

### Método HTTP
`POST`

### URL
`http://localhost:8082/api/v1/challenges/{challengeId}/start`

### Headers
- `Content-Type: application/json`

### Body de Solicitud (JSON)
```json
{
  "challengeId": "CHALLENGE_ID_ANTERIOR",
  "studentId": "660e8400-e29b-41d4-a716-446655440001",
  "codeVersionId": "CODE_VERSION_ID_ANTERIOR"
}
```

#### Descripción de Campos:
- `challengeId` (string, requerido): UUID del challenge.
- `studentId` (string, requerido): UUID del estudiante que inicia el challenge.
- `codeVersionId` (string, requerido): UUID de la code version específica.

### Respuesta Esperada
- **Código de Estado**: `201 Created`
- **Body**: Confirmación de que el challenge ha sido iniciado.

### Notas
- Crea automáticamente una solución por defecto con el `defaultCode` de la code version.
- Activa eventos internos como `ChallengeStartedEvent`.

## 6. Obtener el ID de una Solución por su ID

### Método HTTP
`GET`

### URL
`http://localhost:8082/api/v1/solutions/{solutionId}`

### Headers
- Ninguno requerido.

### Parámetros de URL:
- `solutionId` (string, requerido): UUID de la solución.

### Body de Solicitud
Ninguno (GET request).

### Respuesta Esperada
- **Código de Estado**: `200 OK`
- **Body**: Objeto JSON de tipo `SolutionIdResource` que contiene únicamente el ID de la solución.
  ```json
  {
    "id": "uuid-de-la-solucion"
  }
  ```

### Notas
- Este endpoint retorna solo el ID de la solución, no los detalles completos.
- Se usa para confirmar o referenciar la solución en otros endpoints.
- Si la solución no existe, retorna `404 Not Found`.

## 7. Actualizar la Solución con el Código del Estudiante

### Método HTTP
`PUT`

### URL
`http://localhost:8082/api/v1/solutions/{solutionId}`

### Headers
- `Content-Type: application/json`

### Body de Solicitud (JSON)
```json
{
  "code": "#include <iostream>\n\nint fibonacci(int position) {\n    if (position == 0) return 0;\n    if (position == 1) return 1;\n    int a = 0, b = 1;\n    for (int i = 2; i <= position; ++i) {\n        int temp = a + b;\n        a = b;\n        b = temp;\n    }\n    return b;\n}\n\nint main() {\n    int pos;\n    std::cin >> pos;\n    std::cout << fibonacci(pos) << std::endl;\n    return 0;\n}",
  "language": "C_PLUS_PLUS"
}
```

#### Descripción de Campos:
- `code` (string, requerido): Código completo del estudiante, incluyendo la implementación de la función (ej. `fibonacci`).
- `language` (string, requerido): Lenguaje de programación, debe coincidir con la code version.

### Parámetros de URL:
- `solutionId` (string, requerido): UUID de la solución a actualizar.

### Respuesta Esperada
- **Código de Estado**: `200 OK`
- **Body**: Objeto JSON con la solución actualizada.

### Notas
- Reemplaza el `defaultCode` con la implementación completa del estudiante.
- Debe hacerse antes del submit.

## 8. Enviar la Solución para Evaluación (Submit)

### Método HTTP
`POST`

### URL
`http://localhost:8082/api/v1/solutions/{solutionId}/submit`

### Headers
- `Content-Type: application/json`

### Body de Solicitud (JSON)
```json
{
  "code": "#include <iostream>\n\nint fibonacci(int position) {\n    if (position == 0) return 0;\n    if (position == 1) return 1;\n    int a = 0, b = 1;\n    for (int i = 2; i <= position; ++i) {\n        int temp = a + b;\n        a = b;\n        b = temp;\n    }\n    return b;\n}\n\nint main() {\n    int pos;\n    std::cin >> pos;\n    std::cout << fibonacci(pos) << std::endl;\n    return 0;\n}",
  "studentId": "660e8400-e29b-41d4-a716-446655440001"
}
```

#### Descripción de Campos:
- `code` (string, requerido): Código final del estudiante.
- `studentId` (string, requerido): UUID del estudiante.

### Parámetros de URL:
- `solutionId` (string, requerido): UUID de la solución.

### Respuesta Esperada
- **Código de Estado**: `200 OK` o `202 Accepted`
- **Body**: Confirmación de submit, posiblemente con ID de evaluación.

### Notas
- Activa la evaluación vía gRPC al CodeRunner.
- El CodeRunner prepara el entorno (ej. doctest para C++), ejecuta los tests y compara outputs.
- Si `customValidationCode` está presente, se usa para tests personalizados.