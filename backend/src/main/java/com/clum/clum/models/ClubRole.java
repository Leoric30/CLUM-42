package com.clum.clum.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Entidad: roles
 * Catálogo de roles que puede ejercer un miembro dentro de un club.
 * Los roles estándar del sistema (DIRECTOR, SECRETARIO, TESORERO, CONSEJERO,
 * MIEMBRO)
 * se insertan como datos iniciales en la migración V1.
 *
 * Este catálogo es compartido entre todos los clubs; la asignación
 * concreta (quién tiene qué rol en qué club) se registra en
 * {@link UserClubRole}.
 */
@Entity
@Table(name = "roles")
@Getter
@Setter
public class ClubRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nombre único del rol (ej: "DIRECTOR", "SECRETARIO"). Se usa en @PreAuthorize.
     */
    @Column(name = "nombre", nullable = false, unique = true)
    private String name;

    /** Descripción de las responsabilidades de este rol */
    @Column(name = "descripcion")
    private String description;
}
