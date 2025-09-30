# 🚀 Manual de Integración gRPC - Microservice CodeRunner

## 📋 Resumen Ejecutivo

El **Microservice CodeRunner** es un sistema de ejecución de código completamente modular y productivo que utiliza Docker para ejecutar código de forma segura y escalable. Está **100% alineado** con el contrato gRPC definido en `code_runner.proto`.

### 🎯 Características Principales

- **Ejecución Segura**: Aislamiento completo usando contenedores Docker
- **Multi-lenguaje**: Soporte para C++, Python, JavaScript, Java, Go
- **Pipeline Modular**: Arquitectura basada en pasos intercambiables
- **Base de Datos**: Persistencia completa de ejecuciones con PostgreSQL
- **Monitoreo**: Logging estructurado y métricas detalladas
- **Streaming**: Logs en tiempo real vía gRPC streaming
- **Validación Personalizada**: Soporte para validación de código personalizada

## 🔗 Arquitectura del Sistema

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   gRPC Client   │────▶│  gRPC Server    │────▶│  Pipeline       │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                              │                        │
                              ▼                        ▼
                       ┌─────────────────┐    ┌─────────────────┐
                       │   Database      │    │ Docker Executor │
                       │   (PostgreSQL)  │    │ (Multi-language)│
                       └─────────────────┘    └─────────────────┘
```

### ✅ Campos Proto Implementados (100% Compatibilidad)

#### **ExecutionRequest**
```protobuf
message ExecutionRequest {
    string solution_id = 1;        ✅ Implementado
    string challenge_id = 2;       ✅ Implementado
    string student_id = 3;         ✅ Implementado
    string code = 4;               ✅ Implementado
    string language = 5;           ✅ Implementado
    ExecutionConfig config = 6;    ✅ Implementado completo
}
```

#### **ExecutionResponse**
```protobuf
message ExecutionResponse {
    repeated string approved_test_ids = 1;  ✅ Lista exacta de tests pasados
    bool success = 2;                       ✅ Estado final de ejecución
    string message = 3;                     ✅ Mensaje descriptivo
    string execution_id = 4;                ✅ ID único de ejecución
    ExecutionMetadata metadata = 5;         ✅ Métricas completas
    repeated PipelineStep pipeline_steps = 6; ✅ Información de cada paso
}
```

#### **Servicios gRPC Implementados**
- ✅ `ExecuteCode` - Ejecución principal de código
- ✅ `GetExecutionStatus` - Consulta de estado
- ✅ `HealthCheck` - Monitoreo de salud del servicio
- ✅ `StreamExecutionLogs` - Streaming de logs en tiempo real

## 📝 Contrato de Integración

### 🔍 **Estructura de Request Requerida**

Para que el servicio funcione correctamente, el cliente debe enviar:

```protobuf
ExecutionRequest {
    solution_id: "unique_solution_identifier"    // OBLIGATORIO
    challenge_id: "challenge_identifier"         // OBLIGATORIO  
    student_id: "student_identifier"             // OBLIGATORIO
    code: "function solve(input) { ... }"       // OBLIGATORIO - Código de la solución
    language: "python"                          // OBLIGATORIO - ["cpp", "python", "javascript", "java", "go"]
    config: {                                   // OPCIONAL con valores por defecto
        timeout_seconds: 30                     // Default: 30s
        memory_limit_mb: 512                    // Default: 512MB
        enable_network: false                   // Default: false (sin red)
        environment_variables: {}               // Default: vacío
        debug_mode: false                       // Default: false
    }
}
```

### 🎯 **Estructura de Response Garantizada**

```protobuf
ExecutionResponse {
    approved_test_ids: ["test_1", "test_3"]     // IDs de tests que PASARON
    success: true                               // true si TODOS los tests pasaron
    message: "Passed 2/3 tests"                // Resumen descriptivo
    execution_id: "exec_1609459200123456789"   // ID único para tracking
    metadata: {
        started_at: "2024-01-15T10:30:00Z"
        completed_at: "2024-01-15T10:30:02Z"
        execution_time_ms: 2150
        memory_used_mb: 64
        exit_code: 0
        compilation: {                          // Solo para lenguajes compilados
            success: true
            error_message: ""
            warnings: []
            compilation_time_ms: 850
        }
        test_results: [                         // Detalle de CADA test
            {
                test_id: "test_1"
                passed: true
                expected_output: "120"
                actual_output: "120"
                error_message: ""
                execution_time_ms: 500
            }
        ]
    }
    pipeline_steps: [                           // Información de cada paso
        {
            name: "validation"
            status: STEP_STATUS_COMPLETED
            started_at: "2024-01-15T10:30:00Z"
            completed_at: "2024-01-15T10:30:00.1Z"
            message: "Code validation successful"
            error: ""
            step_metadata: {"lines_of_code": "15"}
            step_order: 1
        }
    ]
}
```

## 🔗 Integración con tu Servidor gRPC Existente

### Opción 1: Integración Directa (Recomendada)

El sistema ya incluye un servidor gRPC completo en `internal/server/server.go`. Simplemente úsalo:

```go
// cmd/main.go
package main

import (
    "log"
    "code-runner/internal/server"
    "code-runner/internal/database"
)

func main() {
    // Opción A: Sin base de datos
    grpcServer, err := server.NewServer("50051", "http://challenges-api:8080")
    if err != nil {
        log.Fatal(err)
    }
    
    // Opción B: Con base de datos PostgreSQL
    db, err := database.NewDatabase("postgres://user:pass@localhost/coderunner")
    if err != nil {
        log.Fatal(err)
    }
    grpcServer, err := server.NewServerWithDB("50051", "http://challenges-api:8080", db)
    if err != nil {
        log.Fatal(err)
    }

    // Iniciar servidor
    log.Fatal(grpcServer.Start())
}
```

### Opción 2: Integración en Servidor Existente

Si ya tienes un servidor gRPC, puedes integrar usando el adaptador:

```go
import (
    "code-runner/internal/adapters"
    "code-runner/internal/pipeline"
)

type YourExistingServer struct {
    dockerAdapter *adapters.DockerExecutionAdapter
    logger        pipeline.Logger
}

func (s *YourExistingServer) ExecuteCode(ctx context.Context, req *ExecutionRequest) (*ExecutionResponse, error) {
    // Validar lenguaje soportado
    if err := s.dockerAdapter.ValidateLanguageSupport(req.Language); err != nil {
        return &ExecutionResponse{
            Success: false,
            Message: err.Error(),
        }, nil
    }

    // Preparar datos de ejecución
    data := s.dockerAdapter.PrepareExecutionData(
        req.SolutionId,
        req.ChallengeId, 
        req.StudentId,
        req.Code,
        req.Language,
    )

    // Ejecutar con Docker
    err := s.dockerAdapter.ExecuteWithDocker(ctx, data)
    if err != nil {
        return &ExecutionResponse{
            Success: false,
            Message: err.Error(),
            ExecutionId: data.ExecutionID,
        }, nil
    }

    // Construir respuesta proto-compatible
    return &ExecutionResponse{
        ApprovedTestIds: s.dockerAdapter.GetApprovedTestIDs(data),
        Success:         data.Success,
        Message:         data.Message,
        ExecutionId:     data.ExecutionID,
        Metadata:        s.buildMetadata(data),
        PipelineSteps:   s.buildPipelineSteps(data),
    }, nil
}
```

## 🔧 Configuración Requerida

### Variables de Entorno
```bash
# Docker execution settings
DOCKER_ENABLED=true
DOCKER_IMAGE_PREFIX=levelup/code-runner
DOCKER_MEMORY_LIMIT_MB=512
DOCKER_TIMEOUT_SECONDS=30
DOCKER_NETWORK_DISABLED=true
```

### Construcción de Imágenes Docker
```bash
# Construir todas las imágenes
cd docker && ./build-images.sh

# O construir lenguaje específico
cd docker && ./build-images.sh python
```

## 📊 Flujo de Ejecución Actualizado

```
gRPC Request → Validation → Docker Pipeline → Response
     ↓              ↓              ↓            ↓
ExecutionRequest → TestFetching → DockerExec → ExecutionResponse
                      ↓              ↓
                   TestCases → Generated Code → Container Execution
                                     ↓              ↓
                                Docker Image → Test Results → approved_test_ids[]
```

## 🎯 Ventajas de la Integración

1. **Compatibilidad Total**: Respeta 100% el contrato proto existente
2. **Seguridad Mejorada**: Ejecución aislada en containers Docker
3. **Soporte Multi-lenguaje**: C++, Python, JavaScript, Java, Go
4. **Custom Validation**: Soporte para `customValidationCode`
5. **Métricas Detalladas**: Tiempo de ejecución, memoria, logs detallados

## 🚀 Migration Path

### Opción 1: Gradual (Recomendada)
```go
func (s *CodeExecutionServer) ExecuteCode(ctx context.Context, req *ExecutionRequest) (*ExecutionResponse, error) {
    // Feature flag para Docker execution
    if useDockerExecution(req.Language) {
        return s.executeWithDocker(ctx, req)
    }
    
    // Fallback a ejecución existente
    return s.executeWithLocal(ctx, req)
}
```

### Opción 2: Completa
```go
func (s *CodeExecutionServer) ExecuteCode(ctx context.Context, req *ExecutionRequest) (*ExecutionResponse, error) {
    // Solo Docker execution
    return s.executeWithDocker(ctx, req)
}
```

## 🔍 Testing de Integración

```go
func TestDockerIntegration(t *testing.T) {
    // Test que el sistema Docker responde con formato proto correcto
    server := NewCodeExecutionServer(logger)
    
    req := &ExecutionRequest{
        SolutionId:  "test_solution",
        ChallengeId: "factorial",
        StudentId:   "student_123",
        Code:        "def factorial(n): return 1 if n <= 1 else n * factorial(n-1)",
        Language:    "python",
        Config: &ExecutionConfig{
            TimeoutSeconds: 30,
            MemoryLimitMb:  256,
        },
    }
    
    resp, err := server.ExecuteCode(context.Background(), req)
    assert.NoError(t, err)
    assert.True(t, resp.Success)
    assert.NotEmpty(t, resp.ApprovedTestIds)
    assert.NotEmpty(t, resp.ExecutionId)
}
```

La implementación está **lista para producción** y completamente alineada con tu `.proto` existente! 🎉