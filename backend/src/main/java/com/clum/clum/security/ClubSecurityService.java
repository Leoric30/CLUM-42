package com.clum.clum.security;

import java.util.List;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.clum.clum.repositories.EnrollmentRequestRepository;
import com.clum.clum.repositories.UserClubRoleRepository;

/**
 * Servicio de seguridad a nivel de club.
 * Se usa en @PreAuthorize para verificar si el usuario autenticado
 * tiene autoridad (Director o Secretario) sobre un club específico.
 *
 * Ejemplo de uso:
 * @PreAuthorize("@clubSecurity.isClubAuthority(#clubId)")
 */
@Component("clubSecurity")
public class ClubSecurityService {

    private static final String ROLE_DIRECTOR   = "DIRECTOR";
    private static final String ROLE_SUBDIRECTOR = "SUBDIRECTOR";
    private static final String ROLE_SECRETARY  = "SECRETARIO";
    private static final String ROLE_ADVISOR    = "CONSEJERO";
    private static final String ROLE_ADMIN      = "ROLE_ADMIN";

    private final UserClubRoleRepository userClubRoleRepo;
    private final EnrollmentRequestRepository enrollmentRequestRepo;

    public ClubSecurityService(UserClubRoleRepository userClubRoleRepo,
                               EnrollmentRequestRepository enrollmentRequestRepo) {
        this.userClubRoleRepo = userClubRoleRepo;
        this.enrollmentRequestRepo = enrollmentRequestRepo;
    }

    /** Verdadero si el usuario autenticado es Director del club indicado */
    public boolean isDirectorOfClub(Long clubId) {
        return hasRoleInClub(clubId, ROLE_DIRECTOR);
    }

    /** Verdadero si el usuario autenticado es Secretario del club indicado */
    public boolean isSecretaryOfClub(Long clubId) {
        return hasRoleInClub(clubId, ROLE_SECRETARY);
    }

    /** Verdadero si el usuario autenticado es Consejero del club indicado */
    public boolean isAdvisorOfClub(Long clubId) {
        return hasRoleInClub(clubId, ROLE_ADVISOR);
    }

    /**
     * Verdadero si el usuario es Director, Secretario, o tiene rol ADMIN global.
     *
     * ADMIN se verifica desde las autoridades ya cargadas en el SecurityContext
     * (puestas por CustomUserDetailService al hacer login), sin necesidad de
     * una consulta adicional a la BD.
     *
     * Director y Secretario se verifican en una sola query combinada
     * (existeAutoridadEnRoles) en lugar de dos llamadas separadas.
     */
    public boolean isClubAuthority(Long clubId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()
                || auth instanceof AnonymousAuthenticationToken) {
            return false;
        }
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(ROLE_ADMIN));
        if (isAdmin) return true;

        String email = auth.getName();
        return userClubRoleRepo.existeAutoridadEnRoles(
                email, clubId, List.of(ROLE_DIRECTOR, ROLE_SUBDIRECTOR, ROLE_SECRETARY));
    }

    /**
     * Variante para endpoints de solicitud donde el parámetro es el ID de la
     * solicitud (no del club). Resuelve el clubId desde la BD y delega a
     * isClubAuthority(Long clubId).
     *
     * Uso:
     * @PreAuthorize("@clubSecurity.isClubAuthorityByApplicationId(#id)")
     */
    public boolean isClubAuthorityByApplicationId(Long applicationId) {
        return enrollmentRequestRepo.findById(applicationId)
                .map(req -> isClubAuthority(req.getClub().getId()))
                .orElse(false);
    }

    /**
     * Consulta la BD para verificar si el usuario autenticado tiene el rol
     * especificado en el club indicado.
     * Retorna false si no hay sesión activa o el usuario es anónimo.
     */
    private boolean hasRoleInClub(Long clubId, String roleName) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()
                || auth instanceof AnonymousAuthenticationToken) {
            return false;
        }
        return userClubRoleRepo.existeAutoridad(auth.getName(), clubId, roleName);
    }
}
