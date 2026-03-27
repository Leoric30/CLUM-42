-- =============================================================
-- V1__init_schema.sql
-- Migración inicial: crea todas las tablas del sistema CLUM.
-- Flyway ejecuta este script UNA sola vez y registra la versión
-- en la tabla flyway_schema_history para no repetirlo.
-- =============================================================

-- ──────────────────────────────────────────────────────────────
-- 1. USUARIOS
--    Tabla central. Almacena tanto aspirantes como miembros activos.
--    rolSistema distingue permisos globales (ADMIN, DIRECTIVA_GENERAL, USUARIO, ASPIRANTE)
-- ──────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS usuarios (
    id               BIGSERIAL PRIMARY KEY,
    nombre_completo  VARCHAR(255) NOT NULL,
    correo           VARCHAR(255) NOT NULL UNIQUE,
    telefono         VARCHAR(50),
    password         VARCHAR(255) NOT NULL,
    activo           BOOLEAN      NOT NULL DEFAULT TRUE,
    rol_sistema      VARCHAR(50)  NOT NULL DEFAULT 'USUARIO',
    fecha_registro   TIMESTAMP            DEFAULT NOW()
);

-- ──────────────────────────────────────────────────────────────
-- 2. CLUBS
--    Cada club es una organización independiente dentro del sistema.
-- ──────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS clubs (
    id             BIGSERIAL PRIMARY KEY,
    nombre         VARCHAR(255) NOT NULL UNIQUE,
    descripcion    TEXT,
    activo         BOOLEAN     NOT NULL DEFAULT TRUE,
    fecha_creacion DATE
);

-- ──────────────────────────────────────────────────────────────
-- 3. ROLES (roles del club)
--    Catálogo de roles que puede tener un miembro dentro de un club.
--    Ejemplos: DIRECTOR, SECRETARIO, TESORERO, CONSEJERO
-- ──────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS roles (
    id          BIGSERIAL PRIMARY KEY,
    nombre      VARCHAR(100) NOT NULL UNIQUE,
    descripcion TEXT
);

-- ──────────────────────────────────────────────────────────────
-- 4. USUARIOS_ROLES_CLUBES  (tabla de relación N:M con atributos)
--    Asigna un ROL a un USUARIO dentro de un CLUB específico.
--    Si club_id es NULL, el rol aplica a nivel de Directiva General.
--    UNIQUE (usuario_id, club_id, rol_club_id) evita duplicados activos.
-- ──────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS usuarios_roles_clubes (
    id           BIGSERIAL PRIMARY KEY,
    usuario_id   BIGINT      NOT NULL REFERENCES usuarios(id),
    club_id      BIGINT               REFERENCES clubs(id),
    rol_club_id  BIGINT      NOT NULL REFERENCES roles(id),
    activo       BOOLEAN     NOT NULL DEFAULT TRUE,
    fecha_inicio DATE,
    fecha_fin    DATE
);

-- ──────────────────────────────────────────────────────────────
-- 5. SOLICITUDES_INSCRIPCION
--    Flujo de entrada al sistema: un aspirante solicita unirse a un club.
--    El secretario/director aprueba o rechaza la solicitud.
-- ──────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS solicitudes_inscripcion (
    id                BIGSERIAL PRIMARY KEY,
    usuario_id        BIGINT       NOT NULL REFERENCES usuarios(id),
    club_id           BIGINT       NOT NULL REFERENCES clubs(id),
    estado            VARCHAR(20)  NOT NULL DEFAULT 'PENDIENTE',  -- PENDIENTE | APROBADA | RECHAZADA
    fecha_solicitud   DATE,
    comprobante_pago  VARCHAR(500),  -- URL del archivo en S3
    motivo_rechazo    VARCHAR(500),
    fecha_resolucion  DATE
);

-- ──────────────────────────────────────────────────────────────
-- 6. EVENTOS
--    Juntas, actividades y reuniones organizadas por cada club.
--    El campo tipo diferencia la naturaleza del evento (TipoEvento enum).
-- ──────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS eventos (
    id             BIGSERIAL PRIMARY KEY,
    titulo         VARCHAR(255)  NOT NULL,
    descripcion    VARCHAR(1000),
    tipo           VARCHAR(50)   NOT NULL,  -- JUNTA | ACTIVIDAD | REUNION
    club_id        BIGINT        NOT NULL REFERENCES clubs(id),
    creado_por_id  BIGINT        NOT NULL REFERENCES usuarios(id),
    lugar          VARCHAR(500),
    fecha_evento   TIMESTAMP     NOT NULL,
    fecha_fin      TIMESTAMP,
    activo         BOOLEAN       NOT NULL DEFAULT TRUE,
    fecha_creacion TIMESTAMP     NOT NULL DEFAULT NOW()
);

-- ──────────────────────────────────────────────────────────────
-- 7. ASISTENCIAS
--    Registra confirmación (RSVP) y asistencia real de usuarios a eventos.
--    UNIQUE (evento_id, usuario_id) impide registros duplicados.
-- ──────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS asistencias (
    id                  BIGSERIAL PRIMARY KEY,
    evento_id           BIGINT  NOT NULL REFERENCES eventos(id),
    usuario_id          BIGINT  NOT NULL REFERENCES usuarios(id),
    confirmado          BOOLEAN NOT NULL DEFAULT FALSE,
    asistio             BOOLEAN NOT NULL DEFAULT FALSE,
    fecha_confirmacion  TIMESTAMP,
    UNIQUE (evento_id, usuario_id)
);

-- ──────────────────────────────────────────────────────────────
-- 8. CUOTAS
--    Define el concepto y monto de lo que se cobra en cada club.
--    Los pagos individuales se registran en la tabla 'pagos'.
-- ──────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS cuotas (
    id                BIGSERIAL PRIMARY KEY,
    club_id           BIGINT          NOT NULL REFERENCES clubs(id),
    descripcion       VARCHAR(255)    NOT NULL,   -- "Cuota mensual", "Inscripción", etc.
    monto             NUMERIC(10, 2)  NOT NULL,
    fecha_vencimiento DATE,
    activo            BOOLEAN         NOT NULL DEFAULT TRUE
);

-- ──────────────────────────────────────────────────────────────
-- 9. PAGOS
--    Historial de pagos de cuotas por usuario.
--    registrado_por_id apunta al secretario/tesorero que validó el pago.
-- ──────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS pagos (
    id                  BIGSERIAL PRIMARY KEY,
    cuota_id            BIGINT          NOT NULL REFERENCES cuotas(id),
    usuario_id          BIGINT          NOT NULL REFERENCES usuarios(id),
    monto               NUMERIC(10, 2)  NOT NULL,
    fecha_pago          DATE,
    comprobante         VARCHAR(500),  -- URL del archivo en S3
    estado              VARCHAR(20)   NOT NULL DEFAULT 'PENDIENTE',  -- PENDIENTE | PAGADO | VENCIDO | CANCELADO
    registrado_por_id   BIGINT        REFERENCES usuarios(id)
);

-- ──────────────────────────────────────────────────────────────
-- 10. DOCUMENTOS
--    Centraliza todos los archivos del sistema (actas, reglamentos, etc.)
--    Reemplaza el patrón de URLs dispersas en otras entidades.
-- ──────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS documentos (
    id            BIGSERIAL PRIMARY KEY,
    nombre        VARCHAR(255) NOT NULL,
    url           VARCHAR(500) NOT NULL,   -- URL completa en S3
    tipo          VARCHAR(50)  NOT NULL,   -- TipoDocumento enum
    club_id       BIGINT       REFERENCES clubs(id),      -- null = documento global
    subido_por_id BIGINT       NOT NULL REFERENCES usuarios(id),
    evento_id     BIGINT       REFERENCES eventos(id),    -- null = no asociado a evento
    fecha_subida  TIMESTAMP    NOT NULL DEFAULT NOW(),
    activo        BOOLEAN      NOT NULL DEFAULT TRUE
);

-- ──────────────────────────────────────────────────────────────
-- 11. COMUNICADOS
--    Avisos formales publicados por autoridades del club o la directiva.
--    Si club_id es NULL, el comunicado es para toda la organización.
-- ──────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS comunicados (
    id                BIGSERIAL PRIMARY KEY,
    titulo            VARCHAR(255)  NOT NULL,
    contenido         VARCHAR(5000),
    club_id           BIGINT        REFERENCES clubs(id),   -- null = global
    autor_id          BIGINT        NOT NULL REFERENCES usuarios(id),
    fecha_publicacion TIMESTAMP     NOT NULL DEFAULT NOW(),
    activo            BOOLEAN       NOT NULL DEFAULT TRUE
);

-- ──────────────────────────────────────────────────────────────
-- 12. AUDITORIA_LOG
--    Registro de acciones sensibles: aprobaciones, rechazos, cambios de rol, etc.
--    Proporciona trazabilidad consultable desde la base de datos.
-- ──────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS auditoria_log (
    id           BIGSERIAL PRIMARY KEY,
    accion       VARCHAR(100)  NOT NULL,   -- Ej: "APROBAR_INSCRIPCION"
    usuario_id   BIGINT        REFERENCES usuarios(id),
    entidad      VARCHAR(100),             -- Nombre de la clase afectada
    entidad_id   BIGINT,                   -- ID del registro afectado
    detalles     VARCHAR(2000),
    fecha_accion TIMESTAMP     NOT NULL DEFAULT NOW()
);

-- ──────────────────────────────────────────────────────────────
-- DATOS INICIALES
--    Roles base del sistema que siempre deben existir
-- ──────────────────────────────────────────────────────────────
INSERT INTO roles (nombre, descripcion) VALUES
    ('DIRECTOR',    'Máxima autoridad del club. Puede aprobar o rechazar solicitudes.'),
    ('SECRETARIO',  'Gestiona inscripciones y comunicados del club.'),
    ('TESORERO',    'Administra cuotas y pagos del club.'),
    ('CONSEJERO',   'Miembro del consejo del club con permisos de consulta.'),
    ('MIEMBRO',     'Miembro regular del club.')
ON CONFLICT (nombre) DO NOTHING;
