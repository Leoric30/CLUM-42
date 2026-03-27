package com.clum.clum.models;

import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

/**
 * Entidad: clubs
 * Representa a cada club dentro del sistema.
 * Los miembros y sus roles se gestionan en {@link UserClubRole}.
 */
@Entity
@Table(name = "clubs")
@Getter
@Setter
public class Club {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Nombre único del club (se muestra en listas y formularios) */
    @Column(name = "nombre", nullable = false, unique = true)
    private String name;

    /** Descripción opcional: propósito, actividades del club, etc. */
    @Column(name = "descripcion")
    private String description;

    /** false = club desactivado (no aparece en listados públicos) */
    @Column(name = "activo", nullable = false)
    private boolean active = true;

    /** Fecha de fundación o registro del club en el sistema */
    @Column(name = "fecha_creacion")
    private LocalDate createdAt;
}
