package com.clum.clum.security;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Configuración central de Spring Security con autenticación JWT stateless.
 *
 * Cambios respecto al esquema anterior (sesiones HTTP):
 * - SessionCreationPolicy.STATELESS: el servidor no guarda ninguna sesión.
 * - JwtAuthenticationFilter: lee la cookie "jwt" en cada request y autentica.
 * - Sin formLogin ni logout de Spring: ambos son endpoints REST en AuthController.
 */
@EnableMethodSecurity
@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                // Sin sesiones HTTP — el estado está en el JWT, no en el servidor
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // CSRF desactivado: JWT + SameSite=Strict en la cookie mitigan CSRF
                .csrf(csrf -> csrf.disable())

                // CORS: permite solicitudes del frontend React
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // Rutas públicas vs. protegidas
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/auth/login",           // Genera el JWT
                                "/api/auth/register",        // Auto-registro de nuevos usuarios
                                "/api/enrollments/register", // Formulario público legacy (retrocompat.)
                                "/api/clubs"                 // Listado de clubes (selector del formulario)
                        ).permitAll()
                        .anyRequest().authenticated()
                )

                // Insertar el filtro JWT antes del filtro estándar de usuario/contraseña
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)

                // 401 en JSON cuando el request no está autenticado
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((req, res, exception) -> {
                            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            res.setContentType("application/json");
                            res.setCharacterEncoding("UTF-8");
                            res.getWriter().write("{\"error\":\"Autenticación requerida\"}");
                        }))

                .httpBasic(basic -> basic.disable());

        return http.build();
    }

    /**
     * CORS para el frontend React en desarrollo.
     * En producción reemplazar los orígenes por el dominio real.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(
                "http://localhost:5173",
                "http://localhost:3000"
        ));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    /**
     * Encoder retrocompatible: valida hashes BCrypt legacy (sin prefijo) y
     * hashes nuevos con prefijo {bcrypt}. Actualiza el formato lazily en login.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new ClumPasswordEncoder();
    }

    /**
     * Expone el AuthenticationManager como Bean para inyectarlo en AuthController,
     * que lo usa para autenticar email+password manualmente en el endpoint de login.
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
