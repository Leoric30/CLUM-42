-- =============================================================
-- V3__constraints.sql
-- Refuerzo de integridad de datos con índices UNIQUE parciales.
--
-- Se usan índices PARCIALES (con WHERE) porque las reglas de
-- negocio solo prohíben duplicados en estados ACTIVOS, no en
-- registros históricos (rechazados, cancelados, vencidos).
-- Un UNIQUE normal bloquearía la capacidad de re-solicitar o
-- re-pagar después de que un registro anterior fue descartado.
-- =============================================================

-- ──────────────────────────────────────────────────────────────
-- 1. solicitudes_inscripcion: una sola solicitud PENDIENTE
--    por combinación (usuario, club)
--
--    Problema sin este índice:
--      Un usuario podía enviar múltiples solicitudes al mismo club
--      mientras ninguna era procesada. Cada clic en "Solicitar"
--      generaba una fila nueva, llenando la bandeja del secretario
--      con duplicados del mismo aspirante.
--
--    Qué permite el índice parcial (WHERE estado = 'PENDIENTE'):
--      ✓ Un usuario puede tener exactamente 1 solicitud PENDIENTE
--        por club en cualquier momento.
--      ✓ Si su solicitud fue RECHAZADA, puede volver a solicitar
--        (la nueva fila queda PENDIENTE y no choca con la antigua
--        que ya es RECHAZADA).
--      ✓ Si fue APROBADA, tampoco puede enviar otra (mismo motivo).
--
--    Mejora funcional:
--      El secretario ve la bandeja limpia: exactamente una
--      solicitud por aspirante por club. No necesita filtrar
--      duplicados manualmente.
-- ──────────────────────────────────────────────────────────────
CREATE UNIQUE INDEX IF NOT EXISTS uq_solicitud_pendiente
    ON solicitudes_inscripcion (usuario_id, club_id)
    WHERE estado = 'PENDIENTE';

-- ──────────────────────────────────────────────────────────────
-- 2. pagos: un solo pago ACTIVO (PENDIENTE o PAGADO)
--    por combinación (usuario, cuota)
--
--    Problema sin este índice:
--      Al registrar un pago, nada impedía que el tesorero o el
--      sistema crearan dos filas para el mismo usuario y la misma
--      cuota en estado PENDIENTE. El historial financiero quedaba
--      corrupto: el miembro aparecía como deudor dos veces por la
--      misma obligación.
--
--    Qué permite el índice parcial
--    (WHERE estado IN ('PENDIENTE', 'PAGADO')):
--      ✓ Solo puede existir un pago activo (PENDIENTE o PAGADO)
--        por usuario y cuota.
--      ✓ Si el pago fue CANCELADO o VENCIDO, el tesorero puede
--        crear un pago nuevo para esa misma cuota (el estado
--        anterior ya no bloquea).
--
--    Mejora funcional:
--      Los reportes de morosidad son precisos: un miembro aparece
--      como deudor exactamente una vez por cuota, nunca duplicado.
--      El balance del club refleja la realidad sin sumas erróneas.
-- ──────────────────────────────────────────────────────────────
CREATE UNIQUE INDEX IF NOT EXISTS uq_pago_activo
    ON pagos (cuota_id, usuario_id)
    WHERE estado IN ('PENDIENTE', 'PAGADO');
