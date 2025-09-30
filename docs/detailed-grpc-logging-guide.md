# Logs Detallados para Debugging gRPC - Submit Solution

## 🔍 Resumen de Logs Implementados

Se han agregado **logs extremadamente detallados** en toda la cadena de ejecución del submit solution para capturar **todos los datos** que se envían al microservicio CodeRunner vía gRPC.

## 📋 Niveles de Logging Implementados

### 1. **SolutionCommandServiceImpl** - Nivel más alto
```
🎯 =============== SUBMIT SOLUTION PROCESS STARTED ===============
📋 Submit Solution Command received:
  - Solution ID: 'uuid-123...'
  - Student ID: 'uuid-456...'
  - Code length: 150 characters
  - Code preview: function solve(n) { return n*2; }

🔍 Step 1: Validating solution exists...
✅ Solution found:
  - Code Version ID: 'uuid-789...'
  - Current status: 'NO_TESTED'

🔍 Step 2: Fetching CodeVersion details from external service...
✅ CodeVersion details retrieved:
  - Programming Language: 'javascript'
  - Total Tests: 5
  - Tests summary:
    * Test 1: ID='test_1', Input='5', Expected='10'
    * Test 2: ID='test_2', Input='3', Expected='6'
    ...

🚀 Step 3: Submitting to CodeRunner via gRPC...
📦 Preparing gRPC execution request:
  - Solution ID: 'uuid-123...'
  - Challenge ID (CodeVersion): 'uuid-789...'
  - Student ID: 'uuid-456...'
  - Language: 'javascript'
  - Code: 150 characters
  - Tests to validate: 5
```

### 2. **CodeExecutionGrpcService** - Nivel de aplicación
```
🎯 Starting code execution process for solution submission
📋 Input data validation:
  - Solution ID: 'uuid-123...'
  - Code Version ID (Challenge ID): 'uuid-789...'
  - Student ID: 'uuid-456...'
  - Programming Language: 'javascript'
  - Code length: 150 characters
  - Total tests to validate: 5
  - Code to execute (preview):
    function solve(n) { return n*2; }
  - Tests details:
    * Test 1: ID='test_1', Input='5', Expected='10'
    * Test 2: ID='test_2', Input='3', Expected='6'
    ...

🔍 Checking CodeRunner service availability...
✅ CodeRunner service is available, proceeding with gRPC execution
🚀 Delegating to gRPC client service for actual execution...
```

### 3. **CodeExecutionGrpcClientService** - Nivel gRPC directo
```
🚀 Preparing gRPC request to CodeRunner microservice
📊 Request parameters - Solution ID: uuid-123..., Challenge ID: uuid-789..., Student ID: uuid-456..., Language: javascript

📤 gRPC ExecutionRequest details:
  - Solution ID: 'uuid-123...'
  - Challenge ID: 'uuid-789...'
  - Student ID: 'uuid-456...'
  - Language: 'javascript'
  - Code length: 150 characters
  - Code preview (first 200 chars): 'function solve(n) { return n*2; }'
  - ExecutionConfig:
    * Timeout: 30 seconds
    * Memory Limit: 512 MB
    * Network Enabled: false
    * Debug Mode: false
    * Environment Variables: 0 items

📤 gRPC ExecutionRequest JSON-like representation:
{
  "solution_id": "uuid-123...",
  "challenge_id": "uuid-789...",
  "student_id": "uuid-456...",
  "language": "javascript",
  "code": "function solve(n) { return n*2; }",
  "config": {
    "timeout_seconds": 30,
    "memory_limit_mb": 512,
    "enable_network": false,
    "debug_mode": false,
    "environment_variables": {}
  }
}

🔄 Sending gRPC request to CodeRunner service...

✅ gRPC ExecutionResponse received:
  - Success: true
  - Message: 'All tests passed'
  - Execution ID: 'exec_1609459200123456789'
  - Approved Test IDs: 5 tests passed
  - Approved Test IDs: [test_1, test_2, test_3, test_4, test_5]
  - Execution Metadata:
    * Execution Time: 2150 ms
    * Memory Used: 64 MB
    * Exit Code: 0
    * Started At: 2024-01-15T10:30:00Z
    * Completed At: 2024-01-15T10:30:02Z
    * Test Results: 5 tests executed
      - Test 'test_1': PASSED (500ms)
      - Test 'test_2': PASSED (450ms)
      ...
  - Pipeline Steps: 3 steps executed
    * Step 1: 'validation' - Status: COMPLETED (100ms)
    * Step 2: 'compilation' - Status: COMPLETED (850ms)
    * Step 3: 'execution' - Status: COMPLETED (1200ms)

📥 gRPC ExecutionResponse JSON-like representation:
{
  "success": true,
  "message": "All tests passed",
  "execution_id": "exec_1609459200123456789",
  "approved_test_ids": ["test_1", "test_2", "test_3", "test_4", "test_5"],
  "metadata": {
    "execution_time_ms": 2150,
    "memory_used_mb": 64,
    "exit_code": 0,
    "test_results_count": 5
  },
  "pipeline_steps_count": 3
}
```

### 4. **Resultado Final**
```
🎉 Step 4: Processing execution results...
📊 Final execution summary:
  - Execution Success: true
  - Execution ID: 'exec_1609459200123456789'
  - Tests Passed: 5/5
  - Success Rate: 100.0%
  - Message: 'All tests passed'
  - Execution Details: 'Executed via gRPC CodeRunner service - Execution time: 2150ms, Memory used: 64MB'

✅ Solution executed successfully!
📋 Creating solution report...
  - Solution Report ID: 'report-uuid-987...'

🎯 =============== SUBMIT SOLUTION PROCESS COMPLETED SUCCESSFULLY ===============
```

## 🚀 Características de los Logs

### ✅ **Datos Capturados**
- **IDs completos**: Solution, Challenge, Student, Execution
- **Código fuente**: Preview y longitud total
- **Configuración gRPC**: Timeout, memoria, network, debug
- **Tests**: Detalles de input/output de cada test
- **Métricas**: Tiempo de ejecución, memoria usada, exit code
- **Pipeline steps**: Cada paso con su duración y status
- **JSON equivalent**: Representación estructurada del protocolo

### ⚙️ **Configuración de Logs**
```yaml
logging:
  level:
    # gRPC related logging - DETAILED DEBUGGING
    com.levelupjourney.microservicechallenges.solutions.interfaces.grpc: INFO
    com.levelupjourney.microservicechallenges.solutions.application.internal.outboundservices.grpc: INFO
    com.levelupjourney.microservicechallenges.solutions.application.internal.commandservices: INFO
```

### 🎯 **Casos de Uso**
1. **Debugging gRPC**: Ver exactamente qué se envía/recibe
2. **Performance analysis**: Métricas de tiempo y memoria  
3. **Test validation**: Verificar qué tests pasan/fallan
4. **Error diagnosis**: Trace completo de errores
5. **Integration testing**: Validar protocolo con CodeRunner

## 📝 Ejemplo de Request Completo

Cuando hagas un submit solution, verás logs como:

```
📤 gRPC ExecutionRequest JSON-like representation:
{
  "solution_id": "123e4567-e89b-12d3-a456-426614174000",
  "challenge_id": "987fcdeb-51a2-4567-8901-abcdef123456", 
  "student_id": "456789ab-cdef-1234-5678-90abcdef1234",
  "language": "java",
  "code": "public class Solution { public int factorial(int n) { return n <= 1 ? 1 : n * factorial(n-1); } }",
  "config": {
    "timeout_seconds": 30,
    "memory_limit_mb": 512, 
    "enable_network": false,
    "debug_mode": false,
    "environment_variables": {}
  }
}
```

## 🔧 Para Activar Debug Adicional

Cambia el nivel de logging a DEBUG para ver aún más detalles:

```yaml
logging:
  level:
    com.levelupjourney.microservicechallenges.solutions: DEBUG
```

Esto mostrará también:
- Protobuf text format completo
- Trace de stack en errores
- Health check details
- gRPC connection status

---

**Ahora tienes visibilidad COMPLETA de todos los datos que se envían al microservicio CodeRunner!** 🎉