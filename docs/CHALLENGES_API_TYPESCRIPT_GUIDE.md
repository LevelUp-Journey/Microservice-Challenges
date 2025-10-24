# Challenges API - TypeScript Integration Guide

Esta guía detalla cómo consumir los endpoints de Challenges desde TypeScript siguiendo el patrón Controller → Service → Response.

## Tabla de Contenidos
- [Interfaces TypeScript](#interfaces-typescript)
- [Challenges Endpoints](#challenges-endpoints)
- [Code Versions Endpoints](#code-versions-endpoints)
- [Code Version Tests Endpoints](#code-version-tests-endpoints)
- [Controller Service Pattern](#controller-service-pattern)

---

## Interfaces TypeScript

### Challenge Interfaces

```typescript
// Request Interfaces
interface CreateChallengeRequest {
  name: string;
  description: string;
  experiencePoints: number;
  difficulty: 'BEGINNER' | 'INTERMEDIATE' | 'ADVANCED' | 'EXPERT';
  tags: string[]; // e.g., ["#principiante", "#java", "#loops"]
}

interface UpdateChallengeRequest {
  name?: string;
  description?: string;
  experiencePoints?: number;
  difficulty?: 'BEGINNER' | 'INTERMEDIATE' | 'ADVANCED' | 'EXPERT';
  status?: 'DRAFT' | 'PUBLISHED' | 'HIDDEN';
  tags?: string[];
}

// Response Interfaces
interface ChallengeResponse {
  id: string;
  teacherId: string;
  name: string;
  description: string;
  experiencePoints: number;
  difficulty: 'BEGINNER' | 'INTERMEDIATE' | 'ADVANCED' | 'EXPERT';
  status: 'DRAFT' | 'PUBLISHED' | 'HIDDEN';
  tags: string[];
  createdAt: string;
  updatedAt: string;
}

interface ErrorResponse {
  message: string;
}
```

### Code Version Interfaces

```typescript
// Request Interfaces
interface CreateCodeVersionRequest {
  challengeId: string;
  language: 'CPP' | 'JAVA' | 'PYTHON' | 'JAVASCRIPT';
  defaultCode: string;
  functionName: string; // e.g., "main", "casoFibonacci", "solveProblem"
}

interface UpdateCodeVersionRequest {
  code?: string;
  functionName?: string;
}

// Response Interfaces
interface CodeVersionResponse {
  id: string;
  challengeId: string;
  language: 'CPP' | 'JAVA' | 'PYTHON' | 'JAVASCRIPT';
  initialCode: string;
  functionName: string;
}
```

### Code Version Test Interfaces

```typescript
// Request Interfaces
interface CreateTestRequest {
  codeVersionId: string;
  input: string;
  expectedOutput: string;
  customValidationCode?: string;
  failureMessage?: string;
  isSecret: boolean; // true = test oculto para estudiantes
}

interface UpdateTestRequest {
  input?: string;
  expectedOutput?: string;
  customValidationCode?: string;
  failureMessage?: string;
  isSecret?: boolean;
}

// Response Interfaces
interface TestResponse {
  id: string;
  codeVersionId: string;
  input: string;
  expectedOutput: string;
  customValidationCode: string | null;
  failureMessage: string | null;
  isSecret: boolean;
}
```

---

## Challenges Endpoints

### 1. Create Challenge

**Endpoint:** `POST /api/v1/challenges`

**Descripción:** Crea un nuevo challenge. El teacherId se extrae del token JWT.

**Request:**
```typescript
interface CreateChallengeRequest {
  name: string;
  description: string;
  experiencePoints: number;
  difficulty: 'BEGINNER' | 'INTERMEDIATE' | 'ADVANCED' | 'EXPERT';
  tags: string[]; // Los tags se normalizan automáticamente (lowercase + #)
}
```

**Response:** `ChallengeResponse`

**Ejemplo de uso:**

```typescript
// Service
class ChallengeService {
  private readonly baseUrl = '/api/v1/challenges';

  async createChallenge(
    request: CreateChallengeRequest,
    token: string
  ): Promise<ChallengeResponse> {
    const response = await fetch(this.baseUrl, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`,
      },
      body: JSON.stringify(request),
    });

    if (!response.ok) {
      throw new Error('Failed to create challenge');
    }

    return response.json();
  }
}

// Controller
class ChallengeController {
  constructor(private service: ChallengeService) {}

  async handleCreateChallenge(token: string): Promise<void> {
    const request: CreateChallengeRequest = {
      name: 'Fibonacci Sequence',
      description: 'Implement the Fibonacci sequence algorithm',
      experiencePoints: 100,
      difficulty: 'BEGINNER',
      tags: ['fibonacci', 'recursion', 'principiante'], // Se convierten a #fibonacci, #recursion, #principiante
    };

    try {
      const challenge = await this.service.createChallenge(request, token);
      console.log('Challenge created:', challenge);
    } catch (error) {
      console.error('Error creating challenge:', error);
    }
  }
}
```

---

### 2. Get Challenge by ID

**Endpoint:** `GET /api/v1/challenges/{challengeId}`

**Descripción:** Obtiene un challenge por ID. Validación de acceso:
- PUBLISHED: Todos pueden ver
- DRAFT/HIDDEN: Solo el propietario

**Response:** `ChallengeResponse`

**Ejemplo de uso:**

```typescript
// Service
class ChallengeService {
  async getChallengeById(
    challengeId: string,
    token: string
  ): Promise<ChallengeResponse> {
    const response = await fetch(`${this.baseUrl}/${challengeId}`, {
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${token}`,
      },
    });

    if (response.status === 403) {
      throw new Error('Access denied - not published and not owner');
    }

    if (response.status === 404) {
      throw new Error('Challenge not found');
    }

    return response.json();
  }
}

// Controller
class ChallengeController {
  async handleGetChallenge(challengeId: string, token: string): Promise<void> {
    try {
      const challenge = await this.service.getChallengeById(challengeId, token);
      console.log('Challenge:', challenge);
    } catch (error) {
      console.error('Error fetching challenge:', error);
    }
  }
}
```

---

### 3. Get All Published Challenges

**Endpoint:** `GET /api/v1/challenges`

**Descripción:** Obtiene todos los challenges publicados. Endpoint público.

**Response:** `ChallengeResponse[]`

**Ejemplo de uso:**

```typescript
// Service
class ChallengeService {
  async getAllPublishedChallenges(): Promise<ChallengeResponse[]> {
    const response = await fetch(this.baseUrl, {
      method: 'GET',
    });

    return response.json();
  }
}

// Controller
class ChallengeController {
  async handleGetAllChallenges(): Promise<void> {
    try {
      const challenges = await this.service.getAllPublishedChallenges();
      console.log(`Found ${challenges.length} published challenges`);
    } catch (error) {
      console.error('Error fetching challenges:', error);
    }
  }
}
```

---

### 4. Get Challenges by Teacher ID

**Endpoint:** `GET /api/v1/challenges/teachers/{teacherId}`

**Descripción:** Obtiene challenges de un profesor. Filtrado basado en roles:
- ROLE_STUDENT: Solo ve challenges PUBLISHED
- ROLE_TEACHER/ROLE_ADMIN: Ve todos los challenges

**Response:** `ChallengeResponse[]`

**Ejemplo de uso:**

```typescript
// Service
class ChallengeService {
  async getChallengesByTeacher(
    teacherId: string,
    token: string
  ): Promise<ChallengeResponse[]> {
    const response = await fetch(
      `${this.baseUrl}/teachers/${teacherId}`,
      {
        method: 'GET',
        headers: {
          'Authorization': `Bearer ${token}`,
        },
      }
    );

    return response.json();
  }
}

// Controller
class ChallengeController {
  async handleGetTeacherChallenges(
    teacherId: string,
    token: string
  ): Promise<void> {
    try {
      const challenges = await this.service.getChallengesByTeacher(
        teacherId,
        token
      );
      console.log(`Teacher has ${challenges.length} challenges`);
    } catch (error) {
      console.error('Error fetching teacher challenges:', error);
    }
  }
}
```

---

### 5. Update Challenge

**Endpoint:** `PATCH /api/v1/challenges/{challengeId}`

**Descripción:** Actualiza un challenge existente. Solo el propietario puede actualizar.

**Request:** `UpdateChallengeRequest`

**Response:** `ChallengeResponse`

**Ejemplo de uso:**

```typescript
// Service
class ChallengeService {
  async updateChallenge(
    challengeId: string,
    request: UpdateChallengeRequest,
    token: string
  ): Promise<ChallengeResponse> {
    const response = await fetch(`${this.baseUrl}/${challengeId}`, {
      method: 'PATCH',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`,
      },
      body: JSON.stringify(request),
    });

    if (response.status === 403) {
      throw new Error('Not authorized - not challenge owner');
    }

    if (response.status === 404) {
      throw new Error('Challenge not found');
    }

    return response.json();
  }
}

// Controller
class ChallengeController {
  async handlePublishChallenge(
    challengeId: string,
    token: string
  ): Promise<void> {
    const request: UpdateChallengeRequest = {
      status: 'PUBLISHED',
    };

    try {
      const challenge = await this.service.updateChallenge(
        challengeId,
        request,
        token
      );
      console.log('Challenge published:', challenge);
    } catch (error) {
      console.error('Error publishing challenge:', error);
    }
  }

  async handleUpdateChallengeTags(
    challengeId: string,
    token: string
  ): Promise<void> {
    const request: UpdateChallengeRequest = {
      tags: ['avanzado', 'algoritmos', 'optimizacion'],
    };

    try {
      const challenge = await this.service.updateChallenge(
        challengeId,
        request,
        token
      );
      console.log('Tags updated:', challenge.tags);
    } catch (error) {
      console.error('Error updating tags:', error);
    }
  }
}
```

---

### 6. Delete Challenge

**Endpoint:** `DELETE /api/v1/challenges/{challengeId}`

**Descripción:** Elimina un challenge por ID. Solo el propietario puede eliminar.

**Response:** `204 No Content` (sin body)

**Ejemplo de uso:**

```typescript
// Service
class ChallengeService {
  async deleteChallenge(
    challengeId: string,
    token: string
  ): Promise<void> {
    const response = await fetch(`${this.baseUrl}/${challengeId}`, {
      method: 'DELETE',
      headers: {
        'Authorization': `Bearer ${token}`,
      },
    });

    if (response.status === 403) {
      throw new Error('Not authorized - not challenge owner');
    }

    if (response.status === 404) {
      throw new Error('Challenge not found');
    }

    if (!response.ok) {
      throw new Error('Failed to delete challenge');
    }
  }
}

// Controller
class ChallengeController {
  async handleDeleteChallenge(
    challengeId: string,
    token: string
  ): Promise<void> {
    try {
      await this.service.deleteChallenge(challengeId, token);
      console.log('Challenge deleted successfully');
    } catch (error) {
      console.error('Error deleting challenge:', error);
    }
  }
}
```

---

## Code Versions Endpoints

### 1. Create Code Version

**Endpoint:** `POST /api/v1/challenges/{challengeId}/code-versions`

**Descripción:** Crea una nueva versión de código para un challenge. El teacher debe especificar el nombre de la función que los estudiantes implementarán.

**Request:** `CreateCodeVersionRequest`

**Response:** `CodeVersionResponse`

**Ejemplo de uso:**

```typescript
// Service
class CodeVersionService {
  private readonly baseUrl = '/api/v1/challenges';

  async createCodeVersion(
    challengeId: string,
    request: CreateCodeVersionRequest,
    token: string
  ): Promise<CodeVersionResponse> {
    const response = await fetch(
      `${this.baseUrl}/${challengeId}/code-versions`,
      {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`,
        },
        body: JSON.stringify(request),
      }
    );

    if (!response.ok) {
      throw new Error('Failed to create code version');
    }

    return response.json();
  }
}

// Controller
class CodeVersionController {
  constructor(private service: CodeVersionService) {}

  async handleCreateCppVersion(
    challengeId: string,
    token: string
  ): Promise<void> {
    const request: CreateCodeVersionRequest = {
      challengeId,
      language: 'CPP',
      functionName: 'casoFibonacci', // Nombre de la función que implementarán
      defaultCode: `#include <iostream>
using namespace std;

int casoFibonacci(int n) {
    // Tu código aquí
    return 0;
}

int main() {
    // El sistema llamará a tu función casoFibonacci()
    return 0;
}`,
    };

    try {
      const codeVersion = await this.service.createCodeVersion(
        challengeId,
        request,
        token
      );
      console.log('Code version created:', codeVersion);
    } catch (error) {
      console.error('Error creating code version:', error);
    }
  }

  async handleCreatePythonVersion(
    challengeId: string,
    token: string
  ): Promise<void> {
    const request: CreateCodeVersionRequest = {
      challengeId,
      language: 'PYTHON',
      functionName: 'solve_fibonacci',
      defaultCode: `def solve_fibonacci(n):
    """
    Implementa la secuencia de Fibonacci
    
    Args:
        n: El número n de la secuencia
    
    Returns:
        El valor fibonacci(n)
    """
    # Tu código aquí
    pass

# El sistema llamará a tu función solve_fibonacci()`,
    };

    try {
      const codeVersion = await this.service.createCodeVersion(
        challengeId,
        request,
        token
      );
      console.log('Python version created:', codeVersion);
    } catch (error) {
      console.error('Error creating Python version:', error);
    }
  }
}
```

---

### 2. Get Code Version by ID

**Endpoint:** `GET /api/v1/challenges/{challengeId}/code-versions/{codeVersionId}`

**Descripción:** Obtiene una versión de código específica incluyendo el nombre de la función.

**Response:** `CodeVersionResponse`

**Ejemplo de uso:**

```typescript
// Service
class CodeVersionService {
  async getCodeVersionById(
    challengeId: string,
    codeVersionId: string,
    token: string
  ): Promise<CodeVersionResponse> {
    const response = await fetch(
      `${this.baseUrl}/${challengeId}/code-versions/${codeVersionId}`,
      {
        method: 'GET',
        headers: {
          'Authorization': `Bearer ${token}`,
        },
      }
    );

    if (response.status === 404) {
      throw new Error('Code version not found');
    }

    return response.json();
  }
}

// Controller
class CodeVersionController {
  async handleGetCodeVersion(
    challengeId: string,
    codeVersionId: string,
    token: string
  ): Promise<void> {
    try {
      const codeVersion = await this.service.getCodeVersionById(
        challengeId,
        codeVersionId,
        token
      );
      console.log('Function name:', codeVersion.functionName);
      console.log('Initial code:', codeVersion.initialCode);
    } catch (error) {
      console.error('Error fetching code version:', error);
    }
  }
}
```

---

### 3. Get All Code Versions for Challenge

**Endpoint:** `GET /api/v1/challenges/{challengeId}/code-versions`

**Descripción:** Obtiene todas las versiones de código de un challenge.

**Response:** `CodeVersionResponse[]`

**Ejemplo de uso:**

```typescript
// Service
class CodeVersionService {
  async getCodeVersionsByChallenge(
    challengeId: string,
    token: string
  ): Promise<CodeVersionResponse[]> {
    const response = await fetch(
      `${this.baseUrl}/${challengeId}/code-versions`,
      {
        method: 'GET',
        headers: {
          'Authorization': `Bearer ${token}`,
        },
      }
    );

    return response.json();
  }
}

// Controller
class CodeVersionController {
  async handleGetAllVersions(
    challengeId: string,
    token: string
  ): Promise<void> {
    try {
      const versions = await this.service.getCodeVersionsByChallenge(
        challengeId,
        token
      );
      
      versions.forEach(version => {
        console.log(`${version.language}: function ${version.functionName}()`);
      });
    } catch (error) {
      console.error('Error fetching code versions:', error);
    }
  }
}
```

---

### 4. Update Code Version

**Endpoint:** `PUT /api/v1/challenges/{challengeId}/code-versions/{codeVersionId}`

**Descripción:** Actualiza el código inicial y/o el nombre de la función de una versión.

**Request:** `UpdateCodeVersionRequest`

**Response:** `CodeVersionResponse`

**Ejemplo de uso:**

```typescript
// Service
class CodeVersionService {
  async updateCodeVersion(
    challengeId: string,
    codeVersionId: string,
    request: UpdateCodeVersionRequest,
    token: string
  ): Promise<CodeVersionResponse> {
    const response = await fetch(
      `${this.baseUrl}/${challengeId}/code-versions/${codeVersionId}`,
      {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`,
        },
        body: JSON.stringify(request),
      }
    );

    if (response.status === 404) {
      throw new Error('Code version not found');
    }

    return response.json();
  }
}

// Controller
class CodeVersionController {
  async handleUpdateFunctionName(
    challengeId: string,
    codeVersionId: string,
    token: string
  ): Promise<void> {
    const request: UpdateCodeVersionRequest = {
      functionName: 'calcularFibonacci', // Cambiar nombre de función
    };

    try {
      const updated = await this.service.updateCodeVersion(
        challengeId,
        codeVersionId,
        request,
        token
      );
      console.log('Function name updated to:', updated.functionName);
    } catch (error) {
      console.error('Error updating function name:', error);
    }
  }

  async handleUpdateCode(
    challengeId: string,
    codeVersionId: string,
    token: string
  ): Promise<void> {
    const request: UpdateCodeVersionRequest = {
      code: `// Código inicial mejorado
int casoFibonacci(int n) {
    // Implementa aquí la solución
    if (n <= 1) return n;
    // Continúa...
    return 0;
}`,
    };

    try {
      const updated = await this.service.updateCodeVersion(
        challengeId,
        codeVersionId,
        request,
        token
      );
      console.log('Code updated successfully');
    } catch (error) {
      console.error('Error updating code:', error);
    }
  }
}
```

---

## Code Version Tests Endpoints

### 1. Create Test

**Endpoint:** `POST /api/v1/challenges/{challengeId}/code-versions/{codeVersionId}/tests`

**Descripción:** Crea un test para una versión de código. Los tests pueden ser secretos (ocultos para estudiantes).

**Request:** `CreateTestRequest`

**Response:** `TestResponse`

**Ejemplo de uso:**

```typescript
// Service
class TestService {
  private readonly baseUrl = '/api/v1/challenges';

  async createTest(
    challengeId: string,
    codeVersionId: string,
    request: CreateTestRequest,
    token: string
  ): Promise<TestResponse> {
    const response = await fetch(
      `${this.baseUrl}/${challengeId}/code-versions/${codeVersionId}/tests`,
      {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`,
        },
        body: JSON.stringify(request),
      }
    );

    if (!response.ok) {
      throw new Error('Failed to create test');
    }

    return response.json();
  }
}

// Controller
class TestController {
  constructor(private service: TestService) {}

  async handleCreatePublicTest(
    challengeId: string,
    codeVersionId: string,
    token: string
  ): Promise<void> {
    const request: CreateTestRequest = {
      codeVersionId,
      input: '5',
      expectedOutput: '5',
      failureMessage: 'Para n=5, el fibonacci debería ser 5',
      isSecret: false, // Test visible para estudiantes
    };

    try {
      const test = await this.service.createTest(
        challengeId,
        codeVersionId,
        request,
        token
      );
      console.log('Public test created:', test);
    } catch (error) {
      console.error('Error creating test:', error);
    }
  }

  async handleCreateSecretTest(
    challengeId: string,
    codeVersionId: string,
    token: string
  ): Promise<void> {
    const request: CreateTestRequest = {
      codeVersionId,
      input: '1000',
      expectedOutput: '43466557686937456435688527675040625802564660517371780402481729089536555417949051890403879840079255169295922593080322634775209689623239873322471161642996440906533187938298969649928516003704476137795166849228875',
      failureMessage: 'Fibonacci de números grandes falló',
      isSecret: true, // Test oculto - estudiantes no ven input/output
    };

    try {
      const test = await this.service.createTest(
        challengeId,
        codeVersionId,
        request,
        token
      );
      console.log('Secret test created (hidden from students)');
    } catch (error) {
      console.error('Error creating secret test:', error);
    }
  }

  async handleCreateCustomValidationTest(
    challengeId: string,
    codeVersionId: string,
    token: string
  ): Promise<void> {
    const request: CreateTestRequest = {
      codeVersionId,
      input: '10',
      expectedOutput: '55',
      customValidationCode: `
        // Validación personalizada en JavaScript
        if (actualOutput !== expectedOutput) {
          return {
            passed: false,
            message: 'El resultado no coincide con el esperado'
          };
        }
        return { passed: true };
      `,
      failureMessage: 'La validación personalizada falló',
      isSecret: false,
    };

    try {
      const test = await this.service.createTest(
        challengeId,
        codeVersionId,
        request,
        token
      );
      console.log('Test with custom validation created');
    } catch (error) {
      console.error('Error creating test:', error);
    }
  }
}
```

---

### 2. Get Test by ID

**Endpoint:** `GET /api/v1/challenges/{challengeId}/code-versions/{codeVersionId}/tests/{testId}`

**Descripción:** Obtiene un test específico incluyendo su estado de secreto.

**Response:** `TestResponse`

**Ejemplo de uso:**

```typescript
// Service
class TestService {
  async getTestById(
    challengeId: string,
    codeVersionId: string,
    testId: string,
    token: string
  ): Promise<TestResponse> {
    const response = await fetch(
      `${this.baseUrl}/${challengeId}/code-versions/${codeVersionId}/tests/${testId}`,
      {
        method: 'GET',
        headers: {
          'Authorization': `Bearer ${token}`,
        },
      }
    );

    if (response.status === 404) {
      throw new Error('Test not found');
    }

    return response.json();
  }
}

// Controller
class TestController {
  async handleGetTest(
    challengeId: string,
    codeVersionId: string,
    testId: string,
    token: string
  ): Promise<void> {
    try {
      const test = await this.service.getTestById(
        challengeId,
        codeVersionId,
        testId,
        token
      );
      
      console.log('Test details:');
      console.log('- Secret:', test.isSecret ? 'Yes (hidden)' : 'No (visible)');
      console.log('- Input:', test.input);
      console.log('- Expected:', test.expectedOutput);
    } catch (error) {
      console.error('Error fetching test:', error);
    }
  }
}
```

---

### 3. Get All Tests for Code Version

**Endpoint:** `GET /api/v1/challenges/{challengeId}/code-versions/{codeVersionId}/tests`

**Descripción:** Obtiene todos los tests de una versión de código, incluyendo tests secretos.

**Response:** `TestResponse[]`

**Ejemplo de uso:**

```typescript
// Service
class TestService {
  async getTestsByCodeVersion(
    challengeId: string,
    codeVersionId: string,
    token: string
  ): Promise<TestResponse[]> {
    const response = await fetch(
      `${this.baseUrl}/${challengeId}/code-versions/${codeVersionId}/tests`,
      {
        method: 'GET',
        headers: {
          'Authorization': `Bearer ${token}`,
        },
      }
    );

    return response.json();
  }
}

// Controller
class TestController {
  async handleGetAllTests(
    challengeId: string,
    codeVersionId: string,
    token: string
  ): Promise<void> {
    try {
      const tests = await this.service.getTestsByCodeVersion(
        challengeId,
        codeVersionId,
        token
      );
      
      const publicTests = tests.filter(t => !t.isSecret);
      const secretTests = tests.filter(t => t.isSecret);
      
      console.log(`Total tests: ${tests.length}`);
      console.log(`- Public: ${publicTests.length}`);
      console.log(`- Secret: ${secretTests.length}`);
    } catch (error) {
      console.error('Error fetching tests:', error);
    }
  }
}
```

---

### 4. Update Test

**Endpoint:** `PUT /api/v1/challenges/{challengeId}/code-versions/{codeVersionId}/tests/{testId}`

**Descripción:** Actualiza los detalles de un test incluyendo su estado de secreto.

**Request:** `UpdateTestRequest`

**Response:** `TestResponse`

**Ejemplo de uso:**

```typescript
// Service
class TestService {
  async updateTest(
    challengeId: string,
    codeVersionId: string,
    testId: string,
    request: UpdateTestRequest,
    token: string
  ): Promise<TestResponse> {
    const response = await fetch(
      `${this.baseUrl}/${challengeId}/code-versions/${codeVersionId}/tests/${testId}`,
      {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`,
        },
        body: JSON.stringify(request),
      }
    );

    if (response.status === 404) {
      throw new Error('Test not found');
    }

    return response.json();
  }
}

// Controller
class TestController {
  async handleMakeTestSecret(
    challengeId: string,
    codeVersionId: string,
    testId: string,
    token: string
  ): Promise<void> {
    const request: UpdateTestRequest = {
      isSecret: true, // Convertir test público en secreto
    };

    try {
      const updated = await this.service.updateTest(
        challengeId,
        codeVersionId,
        testId,
        request,
        token
      );
      console.log('Test is now secret');
    } catch (error) {
      console.error('Error updating test:', error);
    }
  }

  async handleUpdateTestCase(
    challengeId: string,
    codeVersionId: string,
    testId: string,
    token: string
  ): Promise<void> {
    const request: UpdateTestRequest = {
      input: '15',
      expectedOutput: '610',
      failureMessage: 'Fibonacci(15) debería ser 610',
    };

    try {
      const updated = await this.service.updateTest(
        challengeId,
        codeVersionId,
        testId,
        request,
        token
      );
      console.log('Test case updated');
    } catch (error) {
      console.error('Error updating test case:', error);
    }
  }

  async handleRevealSecretTest(
    challengeId: string,
    codeVersionId: string,
    testId: string,
    token: string
  ): Promise<void> {
    const request: UpdateTestRequest = {
      isSecret: false, // Hacer visible un test secreto
    };

    try {
      const updated = await this.service.updateTest(
        challengeId,
        codeVersionId,
        testId,
        request,
        token
      );
      console.log('Test is now public');
    } catch (error) {
      console.error('Error revealing test:', error);
    }
  }
}
```

---

## Controller Service Pattern

### Complete Example: Challenge Lifecycle

```typescript
// Main Application Example
class ChallengeApp {
  private challengeService: ChallengeService;
  private codeVersionService: CodeVersionService;
  private testService: TestService;
  
  private challengeController: ChallengeController;
  private codeVersionController: CodeVersionController;
  private testController: TestController;

  constructor() {
    // Initialize services
    this.challengeService = new ChallengeService();
    this.codeVersionService = new CodeVersionService();
    this.testService = new TestService();
    
    // Initialize controllers
    this.challengeController = new ChallengeController(this.challengeService);
    this.codeVersionController = new CodeVersionController(this.codeVersionService);
    this.testController = new TestController(this.testService);
  }

  async createCompleteChallenge(token: string): Promise<void> {
    try {
      // Step 1: Create challenge
      const challengeRequest: CreateChallengeRequest = {
        name: 'Fibonacci Challenge',
        description: 'Implement the Fibonacci sequence',
        experiencePoints: 150,
        difficulty: 'BEGINNER',
        tags: ['fibonacci', 'recursion', 'principiante'],
      };

      const challenge = await this.challengeService.createChallenge(
        challengeRequest,
        token
      );
      console.log('✓ Challenge created:', challenge.id);

      // Step 2: Create C++ code version
      const cppRequest: CreateCodeVersionRequest = {
        challengeId: challenge.id,
        language: 'CPP',
        functionName: 'fibonacci',
        defaultCode: `int fibonacci(int n) {
    // Implementa aquí
    return 0;
}`,
      };

      const cppVersion = await this.codeVersionService.createCodeVersion(
        challenge.id,
        cppRequest,
        token
      );
      console.log('✓ C++ version created:', cppVersion.id);

      // Step 3: Create public test
      const publicTest: CreateTestRequest = {
        codeVersionId: cppVersion.id,
        input: '5',
        expectedOutput: '5',
        failureMessage: 'Test básico falló',
        isSecret: false,
      };

      await this.testService.createTest(
        challenge.id,
        cppVersion.id,
        publicTest,
        token
      );
      console.log('✓ Public test created');

      // Step 4: Create secret test
      const secretTest: CreateTestRequest = {
        codeVersionId: cppVersion.id,
        input: '20',
        expectedOutput: '6765',
        failureMessage: 'Test secreto falló',
        isSecret: true,
      };

      await this.testService.createTest(
        challenge.id,
        cppVersion.id,
        secretTest,
        token
      );
      console.log('✓ Secret test created');

      // Step 5: Publish challenge
      await this.challengeService.updateChallenge(
        challenge.id,
        { status: 'PUBLISHED' },
        token
      );
      console.log('✓ Challenge published');

      console.log('\n✅ Complete challenge created successfully!');
    } catch (error) {
      console.error('❌ Error in challenge creation:', error);
    }
  }
}

// Usage
const app = new ChallengeApp();
const teacherToken = 'eyJhbGciOiJIUzI1NiIs...';
app.createCompleteChallenge(teacherToken);
```

---

## Notas Importantes

### Tags
- Los tags se normalizan automáticamente: lowercase + prefijo `#`
- Ejemplo: `"java"` → `"#java"`, `"#Python"` → `"#python"`

### Function Name
- El `functionName` indica qué función evaluará el sistema
- Ejemplo: Si `functionName = "fibonacci"`, el sistema buscará y ejecutará la función `fibonacci()`
- Es importante que los estudiantes implementen exactamente esa función

### Secret Tests
- `isSecret = true`: El test está oculto para los estudiantes
- Los estudiantes no ven el input ni el output esperado
- Solo ven si pasaron o fallaron el test
- Útil para evaluar casos edge sin revelar la lógica

### Authentication
- Todos los endpoints (excepto GET published challenges) requieren token JWT
- Header: `Authorization: Bearer <token>`
- El userId y roles se extraen del token

### Role-Based Access
- **ROLE_STUDENT**: Solo ve challenges PUBLISHED
- **ROLE_TEACHER**: Ve todos sus challenges (DRAFT, PUBLISHED, HIDDEN)
- **ROLE_ADMIN**: Ve todos los challenges

### Error Handling
- 400: Bad Request (validación)
- 403: Forbidden (sin permisos)
- 404: Not Found
- 500: Internal Server Error
