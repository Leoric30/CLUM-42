package com.clum.clum.models.enums;

/**
 * Clasifica el tipo de archivo almacenado en la entidad Document.
 *
 * ACTA → Documento oficial de una junta o reunión.
 * COMUNICADO → Aviso o comunicado formal del club.
 * COMPROBANTE → Comprobante de pago de cuota o inscripción.
 * REGLAMENTO → Reglamento interno del club.
 * OTRO → Cualquier otro tipo de archivo no categorizado.
 */
public enum DocumentType {
    ACTA,
    COMUNICADO,
    COMPROBANTE,
    REGLAMENTO,
    OTRO
}
