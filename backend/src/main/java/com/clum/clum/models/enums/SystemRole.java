package com.clum.clum.models.enums;

/**
 * Roles globales del sistema (a nivel de aplicación, no de club).
 *
 * ADMIN → Acceso total al sistema. Usuario del equipo técnico.
 * DIRECTIVA_GENERAL → Directiva que supervisa todos los clubs.
 * USUARIO → Miembro activo con acceso normal a la plataforma.
 * ASPIRANTE → Persona que envió una solicitud pero aún no fue aprobada.
 * No puede iniciar sesión hasta que un director lo apruebe.
 */
public enum SystemRole {
    ADMIN,
    DIRECTIVA_GENERAL,
    USUARIO,
    ASPIRANTE
}
