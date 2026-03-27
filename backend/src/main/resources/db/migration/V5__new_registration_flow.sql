-- =============================================================
-- V5__new_registration_flow.sql
-- Cambios para soportar el flujo "registro de usuario primero":
--
--   1. Agrega el estado REINTENTO al CHECK de solicitudes
--   2. Reemplaza el índice parcial uq_solicitud_pendiente por
--      uno que también cubre REINTENTO (un solo estado activo
--      por combinación usuario+club en cualquier momento)
--   3. Agrega columnas de seguimiento de reintento en solicitudes
--   4. Agrega columna password_version en usuarios para la
--      actualización lazy del formato de hash de contraseña
-- =============================================================

-- ──────────────────────────────────────────────────────────────
-- 1. Extender CHECK constraint de solicitudes_inscripcion.estado
--
--    Se agrega 'REINTENTO' para representar el caso en que un
--    aspirante rechazado actualiza su comprobante de pago y
--    vuelve a someter la solicitud sin crear un registro nuevo.
-- ──────────────────────────────────────────────────────────────
ALTER TABLE solicitudes_inscripcion
    DROP CONSTRAINT IF EXISTS chk_estado_solicitud;

ALTER TABLE solicitudes_inscripcion
    ADD CONSTRAINT chk_estado_solicitud
    CHECK (estado IN ('PENDIENTE', 'APROBADA', 'RECHAZADA', 'REINTENTO'));

-- ──────────────────────────────────────────────────────────────
-- 2. Reemplazar índice UNIQUE parcial uq_solicitud_pendiente
--
--    El índice anterior solo cubría estado = 'PENDIENTE'.
--    Con el flujo de reintento también debe bloquearse tener
--    una solicitud en REINTENTO mientras ya existe una PENDIENTE
--    (y viceversa). Ambos estados representan una "solicitud
--    activa" que el director debe resolver.
--
--    Qué permite este índice:
--      ✓ Exactamente una solicitud activa (PENDIENTE o REINTENTO)
--        por combinación (usuario, club) en cualquier momento.
--      ✓ Tras aprobación o rechazo definitivo, el usuario puede
--        volver a solicitar (los estados APROBADA y RECHAZADA
--        quedan fuera del índice parcial).
-- ──────────────────────────────────────────────────────────────
DROP INDEX IF EXISTS uq_solicitud_pendiente;

CREATE UNIQUE INDEX uq_solicitud_activa
    ON solicitudes_inscripcion (usuario_id, club_id)
    WHERE estado IN ('PENDIENTE', 'REINTENTO');

-- ──────────────────────────────────────────────────────────────
-- 3. Columnas de seguimiento de reintento
--
--    fecha_reintento: timestamp de la última vez que el aspirante
--      actualizó su comprobante y pasó a estado REINTENTO.
--    version: contador de cuántas veces se ha reintentado la
--      solicitud. Útil para auditoría y para que el director
--      sepa si está viendo el primer intento o el tercero.
-- ──────────────────────────────────────────────────────────────
ALTER TABLE solicitudes_inscripcion
    ADD COLUMN IF NOT EXISTS fecha_reintento TIMESTAMP;

ALTER TABLE solicitudes_inscripcion
    ADD COLUMN IF NOT EXISTS version INTEGER NOT NULL DEFAULT 0;

-- ──────────────────────────────────────────────────────────────
-- 4. Columna password_version en usuarios
--
--    Permite distinguir el formato del hash almacenado:
--      'bcrypt_plain'  → hash BCrypt sin prefijo ($2a$10$...)
--                        generado por el sistema anterior.
--      'delegating'    → hash con prefijo {bcrypt}$2a$10$...
--                        generado por DelegatingPasswordEncoder.
--
--    La migración del formato es lazy: ocurre la próxima vez
--    que el usuario inicia sesión correctamente. No requiere
--    actualizar todos los hashes en lote.
-- ──────────────────────────────────────────────────────────────
ALTER TABLE usuarios
    ADD COLUMN IF NOT EXISTS password_version VARCHAR(20) NOT NULL DEFAULT 'bcrypt_plain';
