package com.clum.clum.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

/**
 * Tabla: auditoria_log
 * Registro persistente en base de datos de acciones sensibles del sistema.
 * Complementa el logging en archivo (SLF4J) con trazabilidad consultable en BD.
 *
 * Ejemplos de acciones: APROBAR_INSCRIPCION, RECHAZAR_INSCRIPCION,
 * CAMBIAR_ROL, ELIMINAR_USUARIO, PUBLICAR_COMUNICADO, etc.
 */
@Entity
@Table(name = "auditoria_log")
@Getter
@Setter
@NoArgsConstructor
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Identificador legible de la acción realizada */
    @Column(name = "accion", nullable = false)
    private String action;

    /** Usuario que realizó la acción */
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private User user;

    /** Nombre de la clase/entidad afectada, ej: "EnrollmentRequest" */
    @Column(name = "entidad")
    private String entity;

    /** ID del registro afectado */
    @Column(name = "entidad_id")
    private Long entityId;

    /** Descripción libre con contexto adicional */
    @Column(name = "detalles", columnDefinition = "TEXT")
    private String details;

    @Column(name = "fecha_accion", nullable = false)
    private LocalDateTime actionDate;

    @PrePersist
    public void prePersist() {
        if (this.actionDate == null) {
            this.actionDate = LocalDateTime.now();
        }
    }
}
