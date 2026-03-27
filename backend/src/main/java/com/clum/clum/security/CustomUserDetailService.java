package com.clum.clum.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;
import java.util.stream.Collectors;

import com.clum.clum.repositories.UserRepository;
import com.clum.clum.repositories.UserClubRoleRepository;
import com.clum.clum.models.User;
import com.clum.clum.models.enums.SystemRole;

/**
 * Servicio de autenticación de Spring Security.
 * Spring lo llama automáticamente cada vez que alguien intenta iniciar sesión.
 *
 * Carga el usuario por email y construye el objeto UserDetails con todas
 * sus autoridades (rol del sistema + roles dentro de clubs).
 *
 * Implementa UserDetailsPasswordService para la actualización lazy del
 * formato de hash de contraseña: cuando ClumPasswordEncoder detecta un hash
 * sin prefijo {bcrypt} (formato legado), Spring llama updatePassword() tras
 * el login exitoso y re-guarda el hash en el formato nuevo.
 */
@Service
public class CustomUserDetailService implements UserDetailsService, UserDetailsPasswordService {

    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailService.class);

    private final UserRepository userRepo;
    private final UserClubRoleRepository userClubRoleRepo;

    public CustomUserDetailService(
            UserRepository userRepo,
            UserClubRoleRepository userClubRoleRepo) {
        this.userRepo = userRepo;
        this.userClubRoleRepo = userClubRoleRepo;
    }

    /**
     * Llamado por Spring Security al autenticar un usuario.
     *
     * @param email El email ingresado en el formulario de login.
     * @throws UsernameNotFoundException si el email no existe en la BD.
     * @throws DisabledException         si el usuario aún es ASPIRANTE (no aprobado).
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        // Busca al usuario por email en la base de datos
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        // Los aspirantes no pueden iniciar sesión hasta ser aprobados
        if (user.getSystemRole() == SystemRole.ASPIRANTE) {
            throw new DisabledException("Tu cuenta aún no ha sido aprobada por el director");
        }

        // Proyección: solo los nombres de rol activos del usuario en sus clubs.
        // Evita cargar objetos UserClubRole + ClubRole completos cuando solo
        // necesitamos el String del nombre para construir SimpleGrantedAuthority.
        List<GrantedAuthority> authorities = userClubRoleRepo
                .findActiveRoleNames(user.getId())
                .stream()
                .map(name -> new SimpleGrantedAuthority("ROLE_" + name))
                .collect(Collectors.toList());

        // Agrega también el rol global del sistema (ej: ROLE_ADMIN, ROLE_USUARIO)
        authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getSystemRole().name()));

        // Construye el objeto UserDetails que Spring Security usará internamente.
        // La línea .disabled(...) ya no es necesaria porque el throw DisabledException
        // de arriba termina el método antes de llegar aquí cuando el usuario es
        // ASPIRANTE. Mantenerla era código muerto que solo generaba confusión.
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword()) // Contraseña ya hasheada con BCrypt
                .accountLocked(!user.isActive()) // Bloquea cuentas desactivadas
                .authorities(authorities)
                .build();
    }

    /**
     * Llamado automáticamente por Spring Security cuando ClumPasswordEncoder.upgradeEncoding()
     * retorna true (es decir, cuando el hash no tiene prefijo {bcrypt}).
     *
     * Actualiza el hash en la BD con el nuevo formato {bcrypt}$2a$10$...
     * y marca la columna password_version = 'delegating' para trazabilidad.
     *
     * Este método se llama DESPUÉS de un login exitoso, por lo que la
     * contraseña rawPassword ya fue validada correctamente.
     *
     * @param user            UserDetails del usuario que acaba de autenticarse.
     * @param newEncodedPassword Nuevo hash en formato {bcrypt}$2a$10$...
     * @return UserDetails actualizado con el nuevo hash.
     */
    @Override
    public UserDetails updatePassword(UserDetails user, String newEncodedPassword) {
        userRepo.findByEmail(user.getUsername()).ifPresent(entity -> {
            entity.setPassword(newEncodedPassword);
            entity.setPasswordVersion("delegating");
            userRepo.save(entity);
            logger.info("CONTRASEÑA ACTUALIZADA (lazy upgrade): {}", user.getUsername());
        });

        // Retorna un nuevo UserDetails con el password actualizado para que
        // Spring Security continúe la sesión sin errores
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(newEncodedPassword)
                .authorities(user.getAuthorities())
                .accountLocked(!user.isAccountNonLocked())
                .build();
    }
}
