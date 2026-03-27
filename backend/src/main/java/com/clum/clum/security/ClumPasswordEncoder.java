package com.clum.clum.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashMap;
import java.util.Map;

/**
 * Encoder de contraseñas retrocompatible con el sistema anterior.
 *
 * Problema:
 *   Los hashes existentes en la BD son BCrypt sin prefijo (ej: "$2a$10$...")
 *   porque el sistema anterior usaba BCryptPasswordEncoder directamente.
 *   DelegatingPasswordEncoder de Spring espera el prefijo {bcrypt} y falla
 *   si no lo encuentra, bloqueando el login de todos los usuarios existentes.
 *
 * Solución — wrapper con dos estrategias:
 *   · encode()          → siempre produce {bcrypt}$2a$10$... (formato nuevo)
 *   · matches()         → detecta el formato y delega al encoder correcto:
 *                           "$2a$..." → BCryptPasswordEncoder (legacy)
 *                           "{bcrypt}..." → DelegatingPasswordEncoder (nuevo)
 *   · upgradeEncoding() → true si el hash NO tiene prefijo {} (activa lazy upgrade)
 *
 * Actualización lazy:
 *   Spring Security llama automáticamente a UserDetailsPasswordService.updatePassword()
 *   cuando upgradeEncoding() retorna true. Esto re-guarda el hash con el prefijo
 *   {bcrypt} la próxima vez que el usuario inicia sesión exitosamente.
 *   Sin ninguna migración en lote, en semanas todos los usuarios activos
 *   habrán actualizado su hash silenciosamente.
 */
public class ClumPasswordEncoder implements PasswordEncoder {

    private final BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder(10);
    private final PasswordEncoder delegating;

    public ClumPasswordEncoder() {
        Map<String, PasswordEncoder> encoders = new HashMap<>();
        encoders.put("bcrypt", bcrypt);
        delegating = new DelegatingPasswordEncoder("bcrypt", encoders);
    }

    @Override
    public String encode(CharSequence rawPassword) {
        // Todos los nuevos hashes usan DelegatingPasswordEncoder → "{bcrypt}$2a$10$..."
        return delegating.encode(rawPassword);
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        if (encodedPassword == null) return false;

        if (encodedPassword.startsWith("{")) {
            // Hash con prefijo → DelegatingPasswordEncoder
            return delegating.matches(rawPassword, encodedPassword);
        }
        // Hash sin prefijo → BCrypt legado
        return bcrypt.matches(rawPassword, encodedPassword);
    }

    /**
     * Retorna true cuando el hash almacenado NO tiene prefijo.
     * Spring Security usa esto para decidir si debe llamar updatePassword()
     * tras un login exitoso (actualización lazy del formato).
     */
    @Override
    public boolean upgradeEncoding(String encodedPassword) {
        if (encodedPassword == null) return false;
        // Si no empieza con "{", es el formato legado sin prefijo → debe actualizarse
        return !encodedPassword.startsWith("{");
    }
}
