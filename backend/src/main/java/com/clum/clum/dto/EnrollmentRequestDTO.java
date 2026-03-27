package com.clum.clum.dto;

import java.time.LocalDate;

import com.clum.clum.models.enums.RequestStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EnrollmentRequestDTO {

    private Long id;
    private Long userId;
    private String userName;
    private String userEmail;
    private RequestStatus status;

    /**
     * Fecha de solicitud serializada como ISO-8601 (ej. "2024-03-15").
     * Nombrado createdAt para coincidir con la interfaz TypeScript del frontend.
     */
    private LocalDate createdAt;

    // Datos del club — necesarios para la vista "Mis Solicitudes" del aspirante.
    private Long clubId;
    private String clubName;

    /** URL pública del comprobante de pago almacenado en S3 (puede ser null o "Sin comprobante"). */
    private String paymentReceipt;
}
