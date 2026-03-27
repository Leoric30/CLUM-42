package com.clum.clum.repositories;

import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.clum.clum.models.User;
import com.clum.clum.models.enums.SystemRole;

/**
 * Repositorio JPA para la tabla usuarios.
 * Extiende JpaRepository para operaciones CRUD básicas y
 * define consultas personalizadas por email y por rol del sistema.
 */
@Repository
public interface UserRepository
        extends JpaRepository<User, Long> {

    /** Busca el usuario por su email (usado en el proceso de autenticación) */
    Optional<User> findByEmail(String email);

    /** Verifica si ya existe un usuario con ese email (útil al registrar) */
    boolean existsByEmail(String email);

    /**
     * Lista todos los usuarios con un rol de sistema específico (ej: todos los
     * ASPIRANTE)
     */
    List<User> findBySystemRole(SystemRole systemRole);
}
