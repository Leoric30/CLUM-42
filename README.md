# CLUM — Sistema Gestor de Clubes

Sistema monorepo que contiene el **backend** (Spring Boot) y el **frontend** (React + TypeScript).

## Estructura del proyecto

```
clum/
├── backend/                          ← API REST con Spring Boot + PostgreSQL + Flyway
│   └── src/
│       ├── main/java/com/clum/clum/ ← Código fuente (controllers, services, models...)
│       └── main/resources/          ← application.properties, migraciones Flyway
├── frontend/
│   └── CLUM-42/                      ← Interfaz React 19 + TypeScript + Vite
└── .env.example                      ← Plantilla de variables de entorno
```

## Requisitos previos

- Java 17+
- Maven 3.9+ (o usar `./mvnw` incluido en el proyecto)
- PostgreSQL corriendo en `localhost:5432`
- Node.js 18+ y npm
- Cuenta AWS con bucket S3 configurado (para subida de archivos)

## Configuración de variables de entorno

Copia `.env.example` y crea tu archivo `.env` local (nunca se sube al repositorio):

```bash
cp .env.example .env
```

Luego edita `.env` con tus valores reales:

| Variable | Descripción | Requerida en |
|---|---|---|
| `DB_URL` | URL JDBC de PostgreSQL | Producción |
| `DB_USERNAME` | Usuario de la base de datos | Producción |
| `DB_PASSWORD` | Contraseña de la base de datos | Siempre |
| `JWT_SECRET` | Secret HMAC-SHA256 (mín. 32 chars) | Siempre |
| `AWS_ACCESS_KEY` | Clave de acceso AWS | Siempre |
| `AWS_SECRET_KEY` | Clave secreta AWS | Siempre |
| `AWS_REGION` | Región del bucket S3 | Siempre |
| `AWS_BUCKET_NAME` | Nombre del bucket S3 | Siempre |
| `ENROLLMENT_TEMP_PASSWORD` | Contraseña temporal para aspirantes | Siempre |

> En desarrollo, el perfil `dev` (`application-dev.properties`) ya define valores dummy/locales.
> Solo necesitas definir `DB_PASSWORD`, `JWT_SECRET` y las variables de AWS para producción.

## Cómo correr el proyecto

### Backend

```bash
cd backend

# Opción 1: exportar variables de entorno manualmente
export DB_PASSWORD=tu_password
export JWT_SECRET=un-secreto-de-minimo-32-caracteres!!
export AWS_ACCESS_KEY=tu_clave
export AWS_SECRET_KEY=tu_secreto
export AWS_REGION=us-east-1
export AWS_BUCKET_NAME=tu_bucket
export ENROLLMENT_TEMP_PASSWORD=una_clave_temporal

# Opción 2: cargar desde .env (requiere dotenv-cli u otro loader)
# source .env && ./mvnw spring-boot:run

./mvnw spring-boot:run
```

El servidor levanta en `http://localhost:8080`

### Frontend

```bash
cd frontend/CLUM-42
npm install
npm run dev
```

La app React levanta en `http://localhost:5173` y hace proxy de `/api` al backend en `8080`.

## Tecnologías

| Capa | Tecnología |
|---|---|
| Backend | Spring Boot 4.0.2 · Java 17 |
| Persistencia | PostgreSQL · JPA/Hibernate · Flyway |
| Seguridad | Spring Security · JWT (httpOnly cookie) · BCrypt |
| Almacenamiento | AWS S3 |
| Frontend | React 19 · TypeScript 5.9 · Vite 7 |
| HTTP (frontend) | Axios · TanStack React Query |
| Routing | React Router DOM 7 |

## Base de datos

Las migraciones se ejecutan automáticamente al iniciar el backend con Flyway.

Los scripts están en `backend/src/main/resources/db/migration/`:

| Script | Contenido |
|---|---|
| `V1__init_schema.sql` | Schema inicial |
| `V2__...` | Correcciones |
| `V3__...` | Constraints |
| `V4__...` | Nuevos roles |

Para nuevas migraciones, crear archivos con el patrón `V5__descripcion.sql`, etc.

## Roles del sistema

**SystemRole** (roles globales): `ADMIN`, `DIRECTIVA_GENERAL`, `USUARIO`, `ASPIRANTE`

**ClubRole** (roles dentro de un club): `DIRECTOR`, `SUBDIRECTOR`, `SECRETARIO`, `LOGISTICA`, `MEDIA`, `CAPELLAN`, `TESORERO`, `CONSEJERO`, `VOCAL`, `MIEMBRO`

## Ramas sugeridas

| Rama | Propósito |
|---|---|
| `main` | Producción estable |
| `develop` | Integración de features |
| `feature/nombre` | Nuevas funcionalidades |
| `fix/nombre` | Correcciones de bugs |

## Seguridad

- **Nunca** subas credenciales al repositorio.
- Usa `.env` local o variables de entorno del sistema/servidor.
- `.env` y `application-local.properties` están en `.gitignore`.
- Ver `.env.example` como referencia de las variables necesarias.
