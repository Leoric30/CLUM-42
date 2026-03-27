package com.clum.clum.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO para el body del endpoint de rechazo de inscripción.
 * El frontend debe enviar un motivo no vacío para el rechazo.
 *
 * POST /api/enrollments/clubs/{clubId}/requests/{enrollmentRequestId}/reject
 */
@Getter
@Setter
public class RejectEnrollmentRequest {

    /**
     * Motivo del rechazo que se le comunicará al aspirante. No puede estar vacío.
     */
    @NotBlank(message = "El motivo de rechazo es obligatorio")
    private String reason;
}
