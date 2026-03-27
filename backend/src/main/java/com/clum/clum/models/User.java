package com.clum.clum.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import com.clum.clum.models.enums.SystemRole;

import lombok.Getter;
import lombok.Setter;

/**
 * Entidad: usuarios
 * Representa a cualquier persona en el sistema, sin importar su nivel de
 * acceso.
 * Los roles globales se definen con {@link SystemRole}; los roles dentro de
 * un club específico se gestionan en {@link UserClubRole}.
 */
@Entity
@Table(name = "usuarios")
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Nombre completo del usuario (para mostrar en la interfaz) */
    @Column(name = "nombre_completo", nullable = false)
    private String fullName;

    /** Correo único que funciona como nombre de usuario para el login */
    @Column(name = "correo", nullable = false, unique = true)
    private String email;

    @Column(name = "telefono")
    private String phone;

    /** Contraseña hasheada con BCrypt. Nunca guardar texto plano. */
    @Column(name = "password", nullable = false)
    private String password;

    /** false = cuenta bloqueada (el usuario no puede iniciar sesión) */
    @Column(name = "activo", nullable = false)
    private boolean active = true;

    /**
     * Rol global del sistema. Determina permisos a nivel organizacional.
     * ASPIRANTE: en espera de aprobación.
     * USUARIO: miembro activo con acceso básico.
     * DIRECTIVA_GENERAL / ADMIN: acceso ampliado.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "rol_sistema", nullable = false)
    private SystemRole systemRole = SystemRole.USUARIO;

    /** Fecha y hora en que se creó la cuenta */
    @Column(name = "fecha_registro")
    private LocalDateTime registrationDate = LocalDateTime.now();

    /**
     * Formato del hash de contraseña almacenado.
     * 'bcrypt_plain'  → hash BCrypt sin prefijo ($2a$10$...) generado por el sistema anterior.
     * 'delegating'    → hash con prefijo {bcrypt}$2a$10$... (formato nuevo).
     * Se actualiza lazily cuando el usuario inicia sesión (ClumPasswordEncoder + UserDetailsPasswordService).
     */
    @Column(name = "password_version", nullable = false)
    private String passwordVersion = "bcrypt_plain";

    /**
     * Se ejecuta automáticamente ANTES de insertar el registro en BD.
     * Garantiza que systemRole nunca quede null aunque no se asigne explícitamente.
     */
    @PrePersist
    public void prePersist() {
        if (this.systemRole == null) {
            this.systemRole = SystemRole.USUARIO;
        }
    }
}
