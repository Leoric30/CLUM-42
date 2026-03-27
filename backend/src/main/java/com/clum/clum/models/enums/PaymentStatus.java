package com.clum.clum.models.enums;

/**
 * Estados del ciclo de vida de un pago de cuota.
 *
 * PENDIENTE → El pago fue registrado pero aún no se ha confirmado.
 * PAGADO → El secretario o tesorero validó que el pago fue recibido.
 * VENCIDO → Pasó la fecha límite sin pagar.
 * CANCELADO → El pago fue anulado manualmente.
 */
public enum PaymentStatus {
    PENDIENTE,
    PAGADO,
    VENCIDO,
    CANCELADO
}
