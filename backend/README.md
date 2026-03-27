# CLUM — Backend (Spring Boot)

API REST del sistema gestor de clubes.

## 🚀 Correr en desarrollo

```bash
# Desde la carpeta backend/
./mvnw spring-boot:run
```

O desde la raíz del repo:
```bash
cd backend && ./mvnw spring-boot:run
```

El servidor levanta en `http://localhost:8080`

## 🧪 Compilar y ejecutar tests

```bash
./mvnw clean compile     # Solo compilar
./mvnw test              # Correr tests
./mvnw clean package     # Generar el JAR
```

## 📁 Estructura de paquetes

```
src/main/java/com/clum/clum/
├── controllers/      ← Endpoints REST (@RestController)
├── services/         ← Lógica de negocio (@Service)
├── repositories/     ← Acceso a BD (JpaRepository)
├── models/           ← Entidades JPA (@Entity)
│   └── enums/        ← Enumeraciones del dominio
├── security/         ← Configuración de Spring Security
├── dto/              ← Objetos de transferencia de datos
└── ClumApplication   ← Punto de entrada
```

## 🗄️ Migraciones de base de datos (Flyway)

Los scripts SQL están en `src/main/resources/db/migration/`.

**Convención de nombres obligatoria:**
```
V1__init_schema.sql        ← Migración inicial
V2__add_columna_xxx.sql    ← Agregar columna
V3__fix_constraint_xxx.sql ← Corrección
```

> ⚠️ **Nunca** edites un archivo de migración ya ejecutado.
> Flyway detectará el cambio de checksum y fallará al iniciar.

## 🔐 Variables de entorno requeridas

```bash
export AWS_ACCESS_KEY=...
export AWS_SECRET_KEY=...
export AWS_REGION=...
export AWS_BUCKET_NAME=...
```

O crea un archivo `.env` en la raíz del repo (no se sube a Git).

## 🔑 Endpoints principales

| Método | URL | Acceso | Descripción |
|---|---|---|---|
| `POST` | `/api/inscripciones/solicitar` | Público | Enviar solicitud de inscripción |
| `POST` | `/api/inscripciones/clubes/{id}/solicitudes/{id}/aprobar` | Director/Secretario | Aprobar solicitud |
| `POST` | `/api/inscripciones/clubes/{id}/solicitudes/{id}/rechazar` | Director/Secretario | Rechazar solicitud |
| `GET` | `/api/inscripciones/mis-solicitudes` | Autenticado | Ver mis solicitudes |
| `GET` | `/api/clubes/{id}/solicitudes/pendientes` | Autenticado | Ver pendientes del club |
| `GET` | `/login` | Público | Página de login |
