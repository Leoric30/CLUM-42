package com.clum.clum.controllers;

import com.clum.clum.dto.LoginRequest;
import com.clum.clum.dto.RegisterRequest;
import com.clum.clum.dto.RegisterResponse;
import com.clum.clum.models.User;
import com.clum.clum.models.enums.SystemRole;
import com.clum.clum.repositories.UserClubRoleRepository;
import com.clum.clum.repositories.UserRepository;
import com.clum.clum.security.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controlador REST para autenticación.
 * Base URL: /api/auth
 *
 * Con JWT stateless, el login y el logout son endpoints REST normales
 * (ya no los maneja Spring Security internamente).
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserClubRoleRepository userClubRoleRepo;
    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    @Value("${jwt.expiration-ms}")
    private long expirationMs;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtService jwtService,
                          UserClubRoleRepository userClubRoleRepo,
                          UserRepository userRepo,
                          PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userClubRoleRepo = userClubRoleRepo;
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Registro de usuario en el nuevo flujo (usuario se registra solo).
     * Crea la cuenta con SystemRole.USUARIO y active = true.
     * El usuario puede iniciar sesión de inmediato y solicitar clubes.
     *
     * POST /api/auth/register  (público)
     * Body: { "fullName": "...", "email": "...", "phone": "...", "password": "..." }
     *
     * Respuesta (201): { "registered": true, "email": "..." }
     * Respuesta (409): { "error": "El correo ya está registrado" }
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        if (userRepo.existsByEmail(request.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "El correo ya está registrado"));
        }

        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setSystemRole(SystemRole.USUARIO);
        user.setActive(true);
        user.setPasswordVersion("delegating"); // Hash nuevo con prefijo {bcrypt}
        userRepo.save(user);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new RegisterResponse(true, user.getEmail()));
    }

    /**
     * Autentica al usuario y emite un JWT en una cookie httpOnly.
     *
     * POST /api/auth/login
     * Body: { "email": "...", "password": "..." }
     *
     * Respuesta exitosa (200):
     *   Set-Cookie: jwt=eyJ...; HttpOnly; Path=/; Max-Age=86400; SameSite=Strict
     *   Body: { "authenticated": true, "email": "..." }
     *
     * Respuesta fallida (401):
     *   Body: { "error": "Credenciales incorrectas" }
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request,
                                   HttpServletResponse response) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.email(), request.password()));

            String token = jwtService.generateToken(auth.getName());

            Cookie cookie = new Cookie("jwt", token);
            cookie.setHttpOnly(true);          // No accesible desde JavaScript (XSS-safe)
            cookie.setSecure(false);           // true en producción (requiere HTTPS)
            cookie.setPath("/");               // Válida para todas las rutas
            cookie.setMaxAge((int) (expirationMs / 1000)); // 86400 = 24 horas
            cookie.setAttribute("SameSite", "Strict"); // Mitigación CSRF
            response.addCookie(cookie);

            return ResponseEntity.ok(Map.of(
                    "authenticated", true,
                    "email", auth.getName()
            ));

        } catch (DisabledException e) {
            return ResponseEntity.status(403)
                    .body(Map.of("error", "Tu cuenta aún no ha sido aprobada"));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401)
                    .body(Map.of("error", "Credenciales incorrectas"));
        }
    }

    /**
     * Cierra la sesión borrando la cookie JWT del navegador.
     *
     * POST /api/auth/logout
     * Requiere autenticación (el filtro JWT ya verificó el token).
     *
     * Respuesta (200): { "loggedOut": true }
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("jwt", "");
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0); // Expiración inmediata = borrar la cookie
        response.addCookie(cookie);

        return ResponseEntity.ok(Map.of("loggedOut", true));
    }

    /**
     * Retorna el email y roles del usuario autenticado.
     * Usado por React al iniciar la app para saber si hay sesión activa.
     *
     * GET /api/auth/me
     *
     * @return 200 con datos del usuario, o 401 si no hay cookie JWT válida.
     */
    /**
     * Retorna el email, roles del sistema y roles dentro del club del usuario autenticado.
     * Incluye clubRoles (lista de nombres de rol) y clubId (ID del primer club activo).
     * Usado por React para determinar qué dashboard mostrar según el rol del usuario.
     *
     * GET /api/auth/me
     *
     * Respuesta (200):
     * {
     *   "email": "director@club.com",
     *   "roles": ["ROLE_DIRECTOR", "ROLE_USUARIO"],
     *   "clubRoles": ["DIRECTOR"],
     *   "clubId": 1
     * }
     */
    @GetMapping("/me")
    public ResponseEntity<?> me(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401)
                    .body(Map.of("error", "No hay sesión activa"));
        }

        String email = authentication.getName();

        // Roles del sistema (ya cargados en memoria desde el JWT, sin query a BD)
        List<String> systemRoles = authentication.getAuthorities()
                .stream()
                .map(a -> a.getAuthority())
                .collect(Collectors.toList());

        // Roles de club y club ID (una sola query a BD)
        List<UserClubRoleRepository.ActiveClubRole> activeClubRoles =
                userClubRoleRepo.findActiveClubRolesByEmail(email);

        List<String> clubRoles = activeClubRoles.stream()
                .map(UserClubRoleRepository.ActiveClubRole::getRoleName)
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("email", email);
        response.put("roles", systemRoles);
        response.put("clubRoles", clubRoles);

        // clubId: ID del club del primer rol activo (el usuario pertenece a un club a la vez)
        if (!activeClubRoles.isEmpty()) {
            response.put("clubId", activeClubRoles.get(0).getClubId());
        }

        return ResponseEntity.ok(response);
    }
}
