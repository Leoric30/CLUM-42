package com.clum.clum.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

/**
 * Tabla: asistencias
 * Relaciona usuarios con eventos para registrar confirmación y asistencia real.
 * Constraint UNIQUE (evento_id, usuario_id) para evitar registros duplicados.
 */
@Entity
@Table(name = "asistencias", uniqueConstraints = @UniqueConstraint(columnNames = { "evento_id", "usuario_id" }))
@Getter
@Setter
@NoArgsConstructor
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "evento_id")
    private Event event;

    @ManyToOne(optional = false)
    @JoinColumn(name = "usuario_id")
    private User user;

    /** true si el usuario confirmó que iría (RSVP) */
    @Column(name = "confirmado", nullable = false)
    private boolean confirmed = false;

    /** true si efectivamente asistió (lo registra el secretario) */
    @Column(name = "asistio", nullable = false)
    private boolean attended = false;

    @Column(name = "fecha_confirmacion")
    private LocalDateTime confirmationDate;
}
