# Challenge Controller - Swagger Documentation

## 📋 Endpoints Documentados

### 1. POST /api/v1/challenges
**Create Challenge**
- **Summary**: Create challenge
- **Description**: Create a new coding challenge. Teacher ID extracted from JWT token.
- **Responses**:
  - `201`: Challenge created successfully
  - `500`: Internal server error

---

### 2. GET /api/v1/challenges/{challengeId}
**Get Challenge by ID**
- **Summary**: Get challenge by ID
- **Description**: Retrieve a specific challenge. Access control: PUBLISHED challenges are public, DRAFT/HIDDEN require ownership.
- **Responses**:
  - `200`: Challenge retrieved successfully
  - `403`: Access denied - not published and not owner
  - `404`: Challenge not found

---

### 3. GET /api/v1/challenges
**Get All Published Challenges**
- **Summary**: Get all published challenges
- **Description**: Retrieve all challenges with PUBLISHED status. Public endpoint.
- **Responses**:
  - `200`: Published challenges retrieved successfully

---

### 4. GET /api/v1/challenges/teachers/{teacherId}
**Get Challenges by Teacher**
- **Summary**: Get challenges by teacher
- **Description**: Retrieve challenges by teacher ID. Role-based filtering: Students see only PUBLISHED, Teachers/Admins see all.
- **Responses**:
  - `200`: Challenges retrieved successfully

---

### 5. PATCH /api/v1/challenges/{challengeId}
**Update Challenge**
- **Summary**: Update challenge
- **Description**: Update an existing challenge. Only the challenge owner can make updates.
- **Responses**:
  - `200`: Challenge updated successfully
  - `403`: Forbidden - not the challenge owner
  - `404`: Challenge not found
  - `400`: Validation error

---

### 6. DELETE /api/v1/challenges/{challengeId}/tags/{tagId}
**Unassign Tag from Challenge**
- **Summary**: Unassign tag from challenge
- **Description**: Unassign a specific tag from a challenge
- **Responses**:
  - `204`: Tag successfully unassigned from challenge
  - `404`: Challenge or tag not found

---

### 7. GET /api/v1/challenges/{challengeId}/tags
**Get Challenge Tags**
- **Summary**: Get challenge tags
- **Description**: Retrieve all tags associated with a specific challenge
- **Responses**:
  - `200`: Successfully retrieved challenge tags
  - `404`: Challenge not found
  - `400`: Invalid challenge ID format

---

## 🎯 Características de la Documentación

✅ **Concisa**: Descripciones breves y directas  
✅ **Completa**: Cubre todos los endpoints del controller  
✅ **Informativa**: Incluye códigos de respuesta y descripciones  
✅ **Segura**: Documenta validaciones de acceso y ownership  
✅ **Role-Aware**: Menciona filtrado basado en roles  

---

## 🔍 Acceso a Swagger UI

Una vez que la aplicación esté ejecutándose, accede a:
```
http://localhost:8080/swagger-ui.html
```

O si usas SpringDoc:
```
http://localhost:8080/swagger-ui/index.html
```

---

**Fecha:** 20 de Octubre, 2025  
**Estado:** ✅ Documentación Agregada y Compilada  
**Build:** SUCCESS
