package com.clum.clum.repositories;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.clum.clum.models.UserClubRole;
import com.clum.clum.models.ClubRole;

/**
 * Repositorio JPA para la tabla usuarios_roles_clubes.
 * Proporciona consultas para verificar roles, membresías y autoridades
 * dentro de clubs específicos.
 */
@Repository
public interface UserClubRoleRepository
                extends JpaRepository<UserClubRole, Long> {

        /**
         * Proyección que expone el ID del club y el nombre del rol activo
         * de un usuario. Usada por AuthController para enriquecer GET /me.
         */
        interface ActiveClubRole {
                Long getClubId();
                String getRoleName();
        }

        /**
         * Retorna los roles activos de un usuario (por email) junto con el club
         * al que pertenecen. Solo incluye roles dentro de un club (excluye
         * registros de Directiva General donde club_id = NULL).
         * Usada por AuthController para devolver clubRoles y clubId en /me.
         */
        @Query("SELECT u.club.id AS clubId, r.name AS roleName " +
                        "FROM UserClubRole u JOIN u.clubRole r " +
                        "WHERE u.user.email = :email " +
                        "AND u.active = true " +
                        "AND u.club IS NOT NULL")
        List<ActiveClubRole> findActiveClubRolesByEmail(@Param("email") String email);

        /**
         * Verifica si un usuario (por email) tiene un rol activo en un club.
         * Usado por ClubSecurityService en @PreAuthorize.
         */
        @Query("SELECT COUNT(u) > 0 FROM UserClubRole u " +
                        "WHERE u.user.email = :email " +
                        "AND u.club.id = :clubId " +
                        "AND u.clubRole.name = :roleName " +
                        "AND u.active = true")
        boolean existeAutoridad(
                        @Param("email") String email,
                        @Param("clubId") Long clubId,
                        @Param("roleName") String roleName);

        /**
         * Nombres de los roles activos de un usuario en todos sus clubs.
         * Proyección ligera: retorna solo el nombre del rol en lugar de cargar los
         * objetos UserClubRole completos. Usado por CustomUserDetailService al login.
         */
        @Query("SELECT r.name FROM UserClubRole u JOIN u.clubRole r " +
                        "WHERE u.user.id = :userId AND u.active = true")
        List<String> findActiveRoleNames(@Param("userId") Long userId);

        /**
         * Verifica en una sola query si el usuario tiene cualquiera de los roles
         * indicados en el club. Reemplaza dos llamadas separadas a existeAutoridad
         * (DIRECTOR + SECRETARIO) en isClubAuthority, reduciendo a la mitad los
         * round-trips a BD.
         */
        @Query("SELECT COUNT(u) > 0 FROM UserClubRole u " +
                        "WHERE u.user.email = :email " +
                        "AND u.club.id = :clubId " +
                        "AND u.clubRole.name IN :roles " +
                        "AND u.active = true")
        boolean existeAutoridadEnRoles(
                        @Param("email") String email,
                        @Param("clubId") Long clubId,
                        @Param("roles") List<String> roles);

        /** Todos los roles activos de un usuario (en todos sus clubs) */
        List<UserClubRole> findByUserIdAndActiveTrue(Long userId);

        /** Rol activo de un usuario en un club específico */
        Optional<UserClubRole> findByUserIdAndClubIdAndActiveTrue(
                        Long userId,
                        Long clubId);

        /** Verifica si un usuario ya es miembro activo de un club (cualquier rol) */
        boolean existsByUserIdAndClubIdAndActiveTrue(
                        Long userId,
                        Long clubId);

        /** Roles globales del usuario (Directiva General: registros con club null) */
        List<UserClubRole> findByUserIdAndClubIsNullAndActiveTrue(Long userId);

        /** Verifica si el usuario ya tiene un rol específico activo en el club */
        boolean existsByUserIdAndClubIdAndClubRoleAndActiveTrue(
                        Long userId,
                        Long clubId,
                        ClubRole clubRole);
}
