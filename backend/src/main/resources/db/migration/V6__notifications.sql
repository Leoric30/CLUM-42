-- =============================================================
-- V6__notifications.sql
-- Tabla de notificaciones in-app para el sistema de solicitudes.
--
-- Se disparan notificaciones cuando:
--   - Una solicitud es APROBADA
--   - Una solicitud es RECHAZADA
--   - Un aspirante hace REINTENTO (opcional, para directores)
--
-- El diseño es deliberadamente simple: no hay WebSockets.
-- El frontend hace polling ligero (GET /api/notifications/unread-count)
-- cada 60 segundos para actualizar el badge de la campana.
-- =============================================================

CREATE TABLE IF NOT EXISTS notificaciones (
    id             BIGSERIAL PRIMARY KEY,

    -- Usuario destinatario de la notificación
    usuario_id     BIGINT NOT NULL
                   REFERENCES usuarios(id) ON DELETE CASCADE,

    -- Tipo de notificación (valor del enum NotificationType en Java)
    tipo           VARCHAR(50) NOT NULL,

    -- Título corto visible en el dropdown de la campana
    titulo         VARCHAR(255) NOT NULL,

    -- Mensaje completo con contexto (nombre del club, razón de rechazo, etc.)
    mensaje        TEXT NOT NULL,

    -- false = no leída (muestra badge), true = ya vista
    leida          BOOLEAN NOT NULL DEFAULT FALSE,

    -- Entidad relacionada (nombre de la clase Java, p.ej. 'EnrollmentRequest')
    entidad        VARCHAR(100),

    -- ID del registro relacionado (p.ej. ID de la solicitud)
    entidad_id     BIGINT,

    fecha_creacion TIMESTAMP NOT NULL DEFAULT NOW(),

    -- Se rellena cuando el usuario marca como leída
    fecha_lectura  TIMESTAMP
);

-- Índice para la consulta más frecuente:
-- "¿Cuántas notificaciones no leídas tiene este usuario?" (badge)
-- y "Trae las notificaciones no leídas de este usuario" (dropdown)
CREATE INDEX IF NOT EXISTS idx_notificaciones_usuario_leida
    ON notificaciones (usuario_id, leida);

-- Índice para ordenar por fecha en el dropdown (usuario + fecha DESC)
CREATE INDEX IF NOT EXISTS idx_notificaciones_usuario_fecha
    ON notificaciones (usuario_id, fecha_creacion DESC);
