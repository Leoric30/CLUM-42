package com.clum.clum.dto;

import com.clum.clum.models.enums.RequestStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO de respuesta para los endpoints /api/applications.
 * Extiende los datos básicos de EnrollmentRequestDTO agregando campos
 * específicos del flujo de reintento: rejectionReason, resubmittedAt y version.
 */
@Getter
@Setter
public class ApplicationDTO {

    private Long id;
    private Long userId;
    private String userName;
    private String userEmail;
    private RequestStatus status;

    /** Fecha de creación de la solicitud (ISO-8601) */
    private LocalDate createdAt;

    private Long clubId;
    private String clubName;

    /** URL del comprobante de pago en S3 (puede ser null) */
    private String paymentReceipt;

    /** Motivo del rechazo (null si no fue rechazada o tras un reintento) */
    private String rejectionReason;

    /** Timestamp del último reintento (null si nunca se ha reintentado) */
    private LocalDateTime resubmittedAt;

    /**
     * Número de veces que el aspirante ha reintentado esta solicitud.
     * 0 = primer envío sin reintentos.
     */
    private int version;
}
