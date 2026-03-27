package com.clum.clum.models;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.clum.clum.models.enums.RequestStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Entidad: solicitudes_inscripcion
 * Gestiona el proceso de admisión de nuevos miembros a un club.
 *
 * Flujo: un usuario (aspirante) envía una solicitud → el secretario
 * o director la revisa → la aprueba o rechaza con un motivo.
 *
 * Al aprobar: el usuario recibe un rol en el club y su systemRole
 * cambia de ASPIRANTE a USUARIO (si aplica).
 */
@Entity
@Table(name = "solicitudes_inscripcion")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Usuario que solicita unirse al club */
    @ManyToOne(optional = false)
    @JoinColumn(name = "usuario_id")
    @JsonIgnoreProperties({ "solicitudes", "password", "roles" }) // Evita referencias circulares en JSON
    private User user;

    /** Club al que el usuario quiere pertenecer */
    @ManyToOne(optional = false)
    @JoinColumn(name = "club_id")
    private Club club;

    /** Estado actual de la solicitud (ver RequestStatus enum) */
    @Enumerated(EnumType.STRING)
    @Column(name = "estado")
    private RequestStatus status;

    /** Fecha en que se envió la solicitud */
    @Column(name = "fecha_solicitud")
    private LocalDate requestDate;

    /** URL del comprobante de pago en AWS S3 (puede ser null si no aplica) */
    @Column(name = "comprobante_pago")
    private String paymentReceipt;

    /** Motivo del rechazo, llenado por el secretario al rechazar */
    @Column(name = "motivo_rechazo", length = 500)
    private String rejectionReason;

    /** Fecha en que se tomó la decisión (aprobar o rechazar) */
    @Column(name = "fecha_resolucion")
    private LocalDate resolutionDate;

    /**
     * Fecha y hora del último reintento. Se rellena cada vez que el aspirante
     * actualiza su comprobante de pago tras un rechazo (estado → REINTENTO).
     */
    @Column(name = "fecha_reintento")
    private LocalDateTime resubmittedAt;

    /**
     * Número de veces que esta solicitud ha pasado por el ciclo
     * RECHAZADA → REINTENTO. Empieza en 0 (primer envío, sin reintentos).
     */
    @Column(name = "version", nullable = false)
    private int version = 0;
}
