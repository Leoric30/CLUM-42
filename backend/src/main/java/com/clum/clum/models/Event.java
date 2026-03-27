package com.clum.clum.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import com.clum.clum.models.enums.EventType;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

/**
 * Tabla: eventos
 * Centraliza juntas, actividades y reuniones de cada club.
 * Se distinguen por el campo "type" (EventType enum).
 */
@Entity
@Table(name = "eventos")
@Getter
@Setter
@NoArgsConstructor
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "titulo", nullable = false)
    private String title;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false)
    private EventType type;

    /** Club al que pertenece el evento */
    @ManyToOne(optional = false)
    @JoinColumn(name = "club_id")
    private Club club;

    /** Usuario que convocó o registró el evento */
    @ManyToOne(optional = false)
    @JoinColumn(name = "creado_por_id")
    private User createdBy;

    /** Puede ser una dirección física o un enlace a videollamada */
    @Column(name = "lugar")
    private String location;

    @Column(name = "fecha_evento", nullable = false)
    private LocalDateTime eventDate;

    /** Duración opcional */
    @Column(name = "fecha_fin")
    private LocalDateTime endDate;

    @Column(name = "activo", nullable = false)
    private boolean active = true;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }
}
