package com.clum.clum.security;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.clum.clum.models.enums.SystemRole;

/**
 * Servicio de seguridad para verificar roles globales del sistema.
 * Se usa en @PreAuthorize para proteger operaciones de alto nivel.
 *
 * Ejemplo: @PreAuthorize("@userSecurity.isAdmin()")
 *
 * Las autoridades del usuario (ROLE_ADMIN, ROLE_DIRECTIVA_GENERAL, etc.)
 * ya fueron cargadas en el SecurityContext por CustomUserDetailService
 * en el momento del login. Consultarlas desde aquí no requiere ninguna
 * query a la BD — están en memoria durante toda la sesión.
 */
@Component("userSecurity")
public class UserSecurityService {

    /** Verdadero si el usuario autenticado tiene rol ADMIN en el sistema */
    public boolean isAdmin() {
        return hasSystemRole(SystemRole.ADMIN);
    }

    /** Verdadero si el usuario autenticado pertenece a la Directiva General */
    public boolean isGeneralBoard() {
        return hasSystemRole(SystemRole.DIRECTIVA_GENERAL);
    }

    /**
     * Verifica el rol del usuario leyendo las autoridades ya cargadas en el
     * SecurityContext, sin tocar la base de datos.
     *
     * Antes: hacía findByEmail(email) en la BD en cada @PreAuthorize.
     * Ahora: consulta auth.getAuthorities(), que vive en memoria de sesión.
     * El resultado es funcionalmente idéntico pero sin el round-trip a BD.
     */
    private boolean hasSystemRole(SystemRole systemRole) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()
                || auth instanceof AnonymousAuthenticationToken) {
            return false;
        }

        String authority = "ROLE_" + systemRole.name();
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(authority));
    }
}
