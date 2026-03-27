package com.clum.clum.models;

import com.clum.clum.models.enums.NotificationType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entidad: notificaciones
 * Almacena notificaciones in-app generadas por el sistema de solicitudes.
 *
 * Las notificaciones se crean cuando:
 *   - Una solicitud es aprobada   → notifica al aspirante
 *   - Una solicitud es rechazada  → notifica al aspirante
 *   - Hay un reintento (REINTENTO) → notifica al director del club
 *
 * El frontend hace polling ligero cada 60 s en GET /api/notifications/unread-count
 * para actualizar el badge de la campana. No hay WebSockets.
 */
@Entity
@Table(name = "notificaciones")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Usuario destinatario de la notificación */
    @ManyToOne(optional = false)
    @JoinColumn(name = "usuario_id")
    private User user;

    /** Tipo de evento que originó la notificación */
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false)
    private NotificationType tipo;

    /** Título corto visible en el dropdown de la campana */
    @Column(name = "titulo", nullable = false)
    private String titulo;

    /** Mensaje completo con contexto (nombre del club, motivo de rechazo, etc.) */
    @Column(name = "mensaje", nullable = false, columnDefinition = "TEXT")
    private String mensaje;

    /** false = no leída (muestra badge rojo), true = ya vista */
    @Column(name = "leida", nullable = false)
    private boolean leida = false;

    /** Nombre de la clase Java relacionada (p.ej. "EnrollmentRequest") */
    @Column(name = "entidad")
    private String entidad;

    /** ID del registro relacionado (p.ej. ID de la solicitud de inscripción) */
    @Column(name = "entidad_id")
    private Long entidadId;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    /** Se rellena cuando el usuario marca la notificación como leída */
    @Column(name = "fecha_lectura")
    private LocalDateTime fechaLectura;
}
