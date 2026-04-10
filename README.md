# API de Administración de Estaciones — Terpel

API REST desarrollada en Spring Boot para gestionar estaciones de servicio. Permite operaciones CRUD completas con persistencia en base de datos relacional y optimización de consultas mediante caché.

---

## Requisitos previos

| Herramienta | Versión mínima |
|---|---|
| Java | 17+ (probado con Java 25) |
| Gradle | 8+ (incluido via wrapper) |

No se requiere instalar base de datos — se usa H2 embebida en memoria.

---

## Cómo ejecutar el proyecto

### 1. Clonar el repositorio

```bash
git clone <url-del-repositorio>
cd backend
```

### 2. Compilar y ejecutar

```bash
./gradlew bootRun
```

En Windows:

```bash
gradlew.bat bootRun
```

La aplicación arranca en `http://localhost:8080`.

### 3. Ejecutar los tests

```bash
./gradlew test
```

---

## Documentación de la API

Una vez levantada la aplicación, la documentación interactiva Swagger está disponible en:

```
http://localhost:8080/swagger-ui/index.html
```

La consola de H2 (base de datos en memoria) está disponible en:

```
http://localhost:8080/h2-console
```
- **JDBC URL:** `jdbc:h2:mem:terpeldb`
- **Usuario:** `sa`
- **Contraseña:** *(vacía)*

---

## Endpoints disponibles

| Método | Endpoint | Descripción | Código éxito |
|---|---|---|---|
| `POST` | `/api/stations` | Crear estación | `201 Created` |
| `GET` | `/api/stations` | Listar todas las estaciones | `200 OK` |
| `GET` | `/api/stations/{id}` | Obtener estación por ID | `200 OK` |
| `PUT` | `/api/stations/{id}` | Actualizar estación | `200 OK` |
| `DELETE` | `/api/stations/{id}` | Eliminar estación | `204 No Content` |

### Ejemplo de request — Crear estación

```json
POST /api/stations
Content-Type: application/json

{
  "codigo": "EST001",
  "nombre": "Estación Central",
  "direccion": "Calle Principal 123",
  "ciudad": "Bogotá",
  "latitud": 4.6097,
  "longitud": -74.0817,
  "estado": "ACTIVA"
}
```

### Ejemplo de response exitoso

```json
{
  "id": 1,
  "codigo": "EST001",
  "nombre": "Estación Central",
  "direccion": "Calle Principal 123",
  "ciudad": "Bogotá",
  "latitud": 4.60970000,
  "longitud": -74.08170000,
  "estado": "ACTIVA",
  "fechaCreacion": "2026-04-10T09:00:00",
  "fechaActualizacion": "2026-04-10T09:00:00"
}
```

### Códigos de error

| Código | Escenario |
|---|---|
| `400 Bad Request` | Campos obligatorios faltantes o con formato inválido |
| `404 Not Found` | Estación no encontrada por ID |
| `409 Conflict` | Ya existe una estación con el mismo código |
| `500 Internal Server Error` | Error interno inesperado |

---

## Arquitectura y decisiones técnicas

### Estructura de capas

```
controller/     → Recibe requests HTTP, delega al servicio, retorna ResponseEntity
service/        → Lógica de negocio, validaciones de dominio, caché
  impl/         → Implementación concreta del servicio
repository/     → Acceso a datos vía Spring Data JPA
model/
  entity/       → Entidades JPA mapeadas a la base de datos
  dto/          → Objetos de transferencia (request/response), desacoplados de la entidad
mapper/         → Conversión entidad ↔ DTO mediante MapStruct
config/         → Configuración de caché, CORS y OpenAPI
exception/      → GlobalExceptionHandler centralizado, excepciones de dominio
```

### Decisiones técnicas

**Base de datos — H2 en memoria**
Se eligió H2 para facilitar la ejecución local sin dependencias externas. La misma estructura JPA es compatible con PostgreSQL o MySQL cambiando el `datasource` en `application.yaml`.

**Eliminación física**
Se implementó eliminación física (`DELETE` real en base de datos) porque el modelo de negocio de la prueba no define reglas de auditoría ni referencias a estaciones desde otras entidades. Si existieran relaciones dependientes o requisitos de auditoría, se agregaría un campo `deletedAt` y se filtrarían las consultas por `estado != ELIMINADA`.

**Caché con Spring Cache (ConcurrentMapCacheManager)**
Se cachean `obtenerEstacionPorId` y `obtenerTodasLasEstaciones`. El caché se invalida correctamente en cada operación de escritura (crear, actualizar, eliminar) usando `@CacheEvict` y `@Caching`. Se eligió `ConcurrentMapCacheManager` (en memoria) por simplicidad; en producción se reemplazaría por Redis.

**MapStruct para mapeo entidad ↔ DTO**
Evita código boilerplate de conversión manual y garantiza que los IDs de entidad no sean sobreescritos en operaciones de actualización (`@Mapping(target = "id", ignore = true)`).

**Validaciones con Jakarta Bean Validation**
Todas las validaciones de entrada están en el DTO (`@NotBlank`, `@NotNull`, `@Size`, `@DecimalMin`/`@DecimalMax`). El controlador solo declara `@Valid` — la lógica de validación no contamine el servicio.

**GlobalExceptionHandler**
Centraliza el manejo de excepciones con `@RestControllerAdvice`. Evita `try-catch` en controladores y garantiza respuestas de error consistentes en toda la API.

### Supuestos realizados

- El campo `codigo` es único por estación y actúa como identificador de negocio.
- Los estados válidos son únicamente `ACTIVA` e `INACTIVA`.
- No se requiere autenticación ni autorización para esta prueba.
- Las coordenadas siguen rangos estándar: latitud ±90°, longitud ±180°.

---

## Stack tecnológico

| Tecnología | Versión | Uso |
|---|---|---|
| Java | 25 | Lenguaje principal |
| Spring Boot | 4.0.5 | Framework principal |
| Spring Data JPA | (BOM) | Persistencia |
| Spring Cache | (BOM) | Caché en memoria |
| H2 Database | (BOM) | Base de datos embebida |
| MapStruct | 1.6.3 | Mapeo entidad ↔ DTO |
| Lombok | (BOM) | Reducción de boilerplate |
| SpringDoc OpenAPI | 2.8.8 | Documentación Swagger |
| JUnit 5 + Mockito | (BOM) | Tests unitarios |

---

## Tests

El proyecto incluye tres niveles de pruebas:

| Clase | Tipo | Descripción |
|---|---|---|
| `EstacionServiceImplTest` | Unitario (Mockito) | Cubre toda la lógica de negocio del servicio |
| `EstacionControllerTest` | Unitario (MockMvc) | Cubre todos los endpoints HTTP |
| `EstacionIntegrationTest` | Integración (SpringBootTest + H2) | Prueba el flujo completo contra base de datos real |

```bash
./gradlew test
```
