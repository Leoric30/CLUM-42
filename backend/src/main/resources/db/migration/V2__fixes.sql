-- =============================================================
-- V2__fixes.sql
-- Correcciones al schema inicial (V1). Flyway aplica esto una
-- sola vez después de V1 y registra la versión en
-- flyway_schema_history.
--
-- Correcciones incluidas:
--   1. Índices UNIQUE parciales en usuarios_roles_clubes
--   2. CHECK constraints para campos de estado/tipo
--   3. NOT NULL + DEFAULT en fechas que podían quedar NULL
--   4. TEXT en lugar de VARCHAR para contenidos de longitud libre
--   5. ON DELETE SET NULL en auditoria_log.usuario_id
-- =============================================================

-- ──────────────────────────────────────────────────────────────
-- 1. UNIQUE PARCIALES: usuarios_roles_clubes
--
--    Problema: el constraint UNIQUE mencionado en V1 nunca se
--    creó. Además, un UNIQUE normal sobre (usuario_id, club_id,
--    rol_club_id) no funciona cuando club_id es NULL, porque en
--    SQL estándar NULL != NULL, por lo que dos filas con
--    club_id = NULL no colisionan.
--
--    Solución: dos índices parciales que cubren cada caso.
-- ──────────────────────────────────────────────────────────────

-- Caso A: rol dentro de un club específico (club_id NOT NULL)
CREATE UNIQUE INDEX IF NOT EXISTS uq_urc_con_club
    ON usuarios_roles_clubes (usuario_id, club_id, rol_club_id)
    WHERE club_id IS NOT NULL;

-- Caso B: rol de Directiva General (club_id = NULL)
CREATE UNIQUE INDEX IF NOT EXISTS uq_urc_sin_club
    ON usuarios_roles_clubes (usuario_id, rol_club_id)
    WHERE club_id IS NULL;

-- ──────────────────────────────────────────────────────────────
-- 2. CHECK CONSTRAINTS: validación de estados y tipos en la BD
--
--    Los ENUMs de Java ya validan los valores en la capa de
--    aplicación, pero sin CHECK en la BD cualquier INSERT directo
--    por SQL (herramientas externas, scripts de migración manual,
--    etc.) podría insertar un valor inválido sin error.
-- ──────────────────────────────────────────────────────────────

ALTER TABLE usuarios
    ADD CONSTRAINT chk_rol_sistema
    CHECK (rol_sistema IN ('ADMIN', 'DIRECTIVA_GENERAL', 'USUARIO', 'ASPIRANTE'));

ALTER TABLE solicitudes_inscripcion
    ADD CONSTRAINT chk_estado_solicitud
    CHECK (estado IN ('PENDIENTE', 'APROBADA', 'RECHAZADA'));

-- Nota: el comentario en V1 decía PENDIENTE|PAGADO|RECHAZADO,
-- pero el enum EstadoPago de Java tiene VENCIDO y CANCELADO
-- (no RECHAZADO). Este CHECK refleja el enum Java correcto.
ALTER TABLE pagos
    ADD CONSTRAINT chk_estado_pago
    CHECK (estado IN ('PENDIENTE', 'PAGADO', 'VENCIDO', 'CANCELADO'));

ALTER TABLE eventos
    ADD CONSTRAINT chk_tipo_evento
    CHECK (tipo IN ('JUNTA', 'ACTIVIDAD', 'REUNION', 'OTRO'));

ALTER TABLE documentos
    ADD CONSTRAINT chk_tipo_documento
    CHECK (tipo IN ('ACTA', 'COMUNICADO', 'COMPROBANTE', 'REGLAMENTO', 'OTRO'));

-- ──────────────────────────────────────────────────────────────
-- 3. NOT NULL Y DEFAULT EN FECHAS
--
--    Estas columnas no tienen DEFAULT ni NOT NULL en V1, por lo
--    que pueden quedar NULL si la aplicación no las rellena.
--    El UPDATE previo garantiza que no falle el ALTER en filas
--    existentes con NULL.
-- ──────────────────────────────────────────────────────────────

-- clubs.fecha_creacion
UPDATE clubs
    SET fecha_creacion = CURRENT_DATE
    WHERE fecha_creacion IS NULL;
ALTER TABLE clubs
    ALTER COLUMN fecha_creacion SET DEFAULT CURRENT_DATE;
ALTER TABLE clubs
    ALTER COLUMN fecha_creacion SET NOT NULL;

-- solicitudes_inscripcion.fecha_solicitud
UPDATE solicitudes_inscripcion
    SET fecha_solicitud = CURRENT_DATE
    WHERE fecha_solicitud IS NULL;
ALTER TABLE solicitudes_inscripcion
    ALTER COLUMN fecha_solicitud SET DEFAULT CURRENT_DATE;
ALTER TABLE solicitudes_inscripcion
    ALTER COLUMN fecha_solicitud SET NOT NULL;

-- ──────────────────────────────────────────────────────────────
-- 4. TEXT EN LUGAR DE VARCHAR PARA CONTENIDOS LARGOS
--
--    En PostgreSQL TEXT y VARCHAR tienen el mismo rendimiento.
--    Usar TEXT elimina el riesgo de truncado en campos donde
--    la longitud máxima no puede predecirse (descripciones,
--    contenidos, detalles de auditoría con JSON, etc.).
-- ──────────────────────────────────────────────────────────────

ALTER TABLE eventos
    ALTER COLUMN descripcion TYPE TEXT;

ALTER TABLE comunicados
    ALTER COLUMN contenido TYPE TEXT;

-- auditoria_log.detalles puede contener JSON arbitrario
ALTER TABLE auditoria_log
    ALTER COLUMN detalles TYPE TEXT;

-- ──────────────────────────────────────────────────────────────
-- 5. ON DELETE SET NULL EN auditoria_log.usuario_id
--
--    Si un usuario es eliminado del sistema, sus registros de
--    auditoría deben conservarse (trazabilidad legal/compliance).
--    Con ON DELETE SET NULL el log queda con usuario_id = NULL
--    en lugar de bloquear el borrado o perder el registro.
-- ──────────────────────────────────────────────────────────────

ALTER TABLE auditoria_log
    DROP CONSTRAINT IF EXISTS auditoria_log_usuario_id_fkey;

ALTER TABLE auditoria_log
    ADD CONSTRAINT auditoria_log_usuario_id_fkey
    FOREIGN KEY (usuario_id)
    REFERENCES usuarios(id)
    ON DELETE SET NULL;
