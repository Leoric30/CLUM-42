package com.clum.clum.config;

import com.clum.clum.models.Club;
import com.clum.clum.models.ClubRole;
import com.clum.clum.models.User;
import com.clum.clum.models.UserClubRole;
import com.clum.clum.models.enums.SystemRole;
import com.clum.clum.repositories.ClubRepository;
import com.clum.clum.repositories.ClubRoleRepository;
import com.clum.clum.repositories.EnrollmentRequestRepository;
import com.clum.clum.repositories.UserClubRoleRepository;
import com.clum.clum.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Profile("dev")
@RequiredArgsConstructor
public class DevDataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ClubRepository clubRepository;
    private final ClubRoleRepository clubRoleRepository;
    private final UserClubRoleRepository userClubRoleRepository;
    private final EnrollmentRequestRepository enrollmentRequestRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        System.out.println("🌱 Loading development data...");

        clearData();
        ensureRoles();
        ensureClubs();
        createUsers();
        assignRoles();

        System.out.println("✅ Development data loaded successfully");
    }

    // ─────────────────────────────────────────────────────────────
    // LIMPIEZA: borra datos mutables respetando el orden de FKs
    // No toca: clubs ni roles (datos de referencia)
    // ─────────────────────────────────────────────────────────────

    private void clearData() {

        userClubRoleRepository.deleteAll();
        enrollmentRequestRepository.deleteAll();
        userRepository.deleteAll();

    }

    // ─────────────────────────────────────────────────────────────
    // ROLES: garantiza que todos los roles del catálogo existen.
    // Idempotente: usa ON CONFLICT equivalente en Java.
    // ─────────────────────────────────────────────────────────────

    private void ensureRoles() {

        saveRoleIfAbsent("DIRECTOR",    "Máxima autoridad del club. Puede aprobar o rechazar solicitudes.");
        saveRoleIfAbsent("SUBDIRECTOR", "Apoyo directo al Director. Acceso a revisiones y propuestas.");
        saveRoleIfAbsent("SECRETARIO",  "Gestiona inscripciones y comunicados del club.");
        saveRoleIfAbsent("TESORERO",    "Administra cuotas y pagos del club.");
        saveRoleIfAbsent("LOGISTICA",   "Encargado de planes de trabajo, tareas y organización de eventos.");
        saveRoleIfAbsent("MEDIA",       "Responsable del contenido multimedia y comunicación visual del club.");
        saveRoleIfAbsent("CAPELLAN",    "Encargado del ámbito espiritual y actividades de integración.");
        saveRoleIfAbsent("CONSEJERO",   "Miembro del consejo del club con permisos de consulta.");
        saveRoleIfAbsent("VOCAL",       "Miembro vocal del club.");
        saveRoleIfAbsent("MIEMBRO",     "Miembro regular del club.");

    }

    private void saveRoleIfAbsent(String name, String description) {

        if (clubRoleRepository.findByName(name).isEmpty()) {
            ClubRole role = new ClubRole();
            role.setName(name);
            role.setDescription(description);
            clubRoleRepository.save(role);
        }

    }

    // ─────────────────────────────────────────────────────────────
    // CLUBS: garantiza que los clubs de prueba existen.
    // ─────────────────────────────────────────────────────────────

    private void ensureClubs() {

        saveClubIfAbsent("Guias Mayores", "Club de Guias Mayores");
        saveClubIfAbsent("Medallones",    "Club de Medallones");
        saveClubIfAbsent("Embajadores",   "Club de Embajadores");

    }

    private void saveClubIfAbsent(String name, String description) {

        if (clubRepository.findByName(name).isEmpty()) {
            Club club = new Club();
            club.setName(name);
            club.setDescription(description);
            club.setActive(true);
            clubRepository.save(club);
        }

    }

    // ─────────────────────────────────────────────────────────────
    // USUARIOS: crea los usuarios de prueba frescos en cada run
    // ─────────────────────────────────────────────────────────────

    private void createUsers() {

        createUser("Abdaly Camacho",  "abdaly@clum.test");
        createUser("Eric Lopez",      "eric@clum.test");
        createUser("Ana Monzon",      "ana@clum.test");
        createUser("Keyri Cahuich",   "keyri@clum.test");
        createUser("Adiel Chavira",   "adiel@clum.test");
        createUser("Arlet Bolio",     "arlet@clum.test");
        createUser("Ruth Martinez",   "ruth@clum.test");
        createUser("Alfredo Sandoval","alfredo@clum.test");

    }

    private void createUser(String fullName, String email) {

        User user = new User();
        user.setFullName(fullName);
        user.setEmail(email);
        user.setPhone("8110000000");
        user.setPassword(passwordEncoder.encode("12345prueba"));
        user.setSystemRole(SystemRole.USUARIO);
        user.setActive(true);

        userRepository.save(user);

    }

    // ─────────────────────────────────────────────────────────────
    // ASIGNACIÓN DE ROLES: asigna un rol a cada usuario en el club
    // ─────────────────────────────────────────────────────────────

    private void assignRoles() {

        Club club = clubRepository.findByName("Guias Mayores")
                .orElseThrow(() -> new IllegalStateException("Club 'Guias Mayores' no encontrado"));

        assignRole("abdaly@clum.test", "DIRECTOR",    club);
        assignRole("eric@clum.test",   "SUBDIRECTOR", club);
        assignRole("ana@clum.test",    "SUBDIRECTOR", club);
        assignRole("keyri@clum.test",  "SECRETARIO",  club);
        assignRole("adiel@clum.test",  "TESORERO",    club);
        assignRole("arlet@clum.test",  "MEDIA",       club);
        assignRole("ruth@clum.test",   "LOGISTICA",   club);
        assignRole("alfredo@clum.test","CAPELLAN",    club);

    }

    private void assignRole(String email, String roleName, Club club) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Usuario no encontrado: " + email));

        ClubRole clubRole = clubRoleRepository.findByName(roleName)
                .orElseThrow(() -> new IllegalStateException("Rol no encontrado: " + roleName));

        UserClubRole userRoleClub = new UserClubRole();
        userRoleClub.setUser(user);
        userRoleClub.setClub(club);
        userRoleClub.setClubRole(clubRole);
        userRoleClub.setActive(true);

        userClubRoleRepository.save(userRoleClub);

    }

}
