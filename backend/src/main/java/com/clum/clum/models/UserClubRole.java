package com.clum.clum.models;

import jakarta.persistence.*;
import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Entidad: usuarios_roles_clubes
 * Tabla de relación entre User, Club y ClubRole.
 * Responde la pregunta: "¿Qué rol tiene X persona dentro de Y club?"
 *
 * Si club es null, el rol aplica a nivel de Directiva General (toda la
 * organización).
 * El campo active permite conservar historial sin borrar registros.
 */
@Entity
@Table(name = "usuarios_roles_clubes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserClubRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Persona a quien se le asigna el rol */
    @ManyToOne(optional = false)
    @JoinColumn(name = "usuario_id")
    private User user;

    /** Club al que pertenece la asignación. null = Directiva General */
    @ManyToOne
    @JoinColumn(name = "club_id")
    private Club club;

    /** Rol que ejerce el usuario dentro del club */
    @ManyToOne(optional = false)
    @JoinColumn(name = "rol_club_id")
    private ClubRole clubRole;

    /** false = membresía terminada, se conserva para historial */
    @Column(name = "activo", nullable = false)
    private boolean active = true;

    /** Cuándo inició la membresía */
    @Column(name = "fecha_inicio")
    private LocalDate startDate;

    /** Cuándo terminó la membresía (null = vigente) */
    @Column(name = "fecha_fin")
    private LocalDate endDate;

    /**
     * Se ejecuta antes de insertar el registro.
     * Asigna la fecha de inicio si no fue proporcionada explícitamente.
     */
    @PrePersist
    public void prePersist() {
        if (this.startDate == null) {
            this.startDate = LocalDate.now();
        }
    }
}
