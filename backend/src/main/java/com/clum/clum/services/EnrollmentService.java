package com.clum.clum.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import java.io.File;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.EntityNotFoundException;

import com.clum.clum.repositories.EnrollmentRequestRepository;
import com.clum.clum.repositories.UserRepository;
import com.clum.clum.repositories.UserClubRoleRepository;
import com.clum.clum.repositories.ClubRepository;
import com.clum.clum.repositories.ClubRoleRepository;
import com.clum.clum.models.Club;
import com.clum.clum.models.User;
import com.clum.clum.models.EnrollmentRequest;
import com.clum.clum.models.UserClubRole;
import com.clum.clum.models.ClubRole;
import com.clum.clum.models.enums.NotificationType;
import com.clum.clum.models.enums.RequestStatus;
import com.clum.clum.models.enums.SystemRole;
import com.clum.clum.dto.ApplicationDTO;
import com.clum.clum.dto.EnrollmentRequestDTO;

/**
 * Servicio principal del flujo de inscripción.
 * Contiene la lógica de negocio para:
 * - Crear solicitudes de inscripción (desde formulario público)
 * - Aprobar solicitudes (secretario/director asigna rol al nuevo miembro)
 * - Rechazar solicitudes (secretario/director indica el motivo)
 * - Consultar solicitudes del usuario en sesión
 */
@Service
public class EnrollmentService {

    private static final Logger logger = LoggerFactory.getLogger(EnrollmentService.class);

    @Value("${enrollment.temporary-password}")
    private String temporaryPassword;

    // Estados que representan una "solicitud activa" (bloquean un segundo envío)
    private static final List<RequestStatus> ACTIVE_STATUSES =
            List.of(RequestStatus.PENDIENTE, RequestStatus.REINTENTO);

    private final PasswordEncoder passwordEncoder;

    private final EnrollmentRequestRepository enrollmentRequestRepo;
    private final ClubRepository clubRepo;
    private final UserClubRoleRepository userClubRoleRepo;
    private final ClubRoleRepository clubRoleRepo;
    private final UserRepository userRepo;
    private final S3Service s3Service;
    private final NotificationService notificationService;

    public EnrollmentService(
            EnrollmentRequestRepository enrollmentRequestRepo,
            ClubRepository clubRepo,
            UserClubRoleRepository userClubRoleRepo,
            ClubRoleRepository clubRoleRepo,
            UserRepository userRepo,
            PasswordEncoder passwordEncoder,
            S3Service s3Service,
            NotificationService notificationService) {
        this.enrollmentRequestRepo = enrollmentRequestRepo;
        this.clubRepo = clubRepo;
        this.userClubRoleRepo = userClubRoleRepo;
        this.clubRoleRepo = clubRoleRepo;
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.s3Service = s3Service;
        this.notificationService = notificationService;
    }

    /**
     * Crea una solicitud de inscripción para un usuario ya autenticado.
     * Valida que el club exista y que no haya una solicitud PENDIENTE duplicada.
     *
     * @param user   El usuario que quiere inscribirse.
     * @param clubId ID del club al que quiere unirse.
     * @return La solicitud creada y guardada en BD.
     */
    @Transactional
    public EnrollmentRequest submitEnrollment(User user, Long clubId) {
        // 1. Verificar que el club existe
        Club club = clubRepo.findById(clubId)
                .orElseThrow(() -> new EntityNotFoundException("Club no encontrado con ID: " + clubId));

        // 2. Evitar solicitudes duplicadas (PENDIENTE o REINTENTO activos)
        boolean exists = enrollmentRequestRepo.existsByUserIdAndClubIdAndStatusIn(
                user.getId(), club.getId(), ACTIVE_STATUSES);
        if (exists) {
            throw new IllegalStateException("Ya tienes una solicitud activa para este club");
        }

        // 3. Crear y persistir la solicitud
        EnrollmentRequest request = new EnrollmentRequest();
        request.setUser(user);
        request.setClub(club);
        request.setStatus(RequestStatus.PENDIENTE);
        request.setRequestDate(LocalDate.now());

        return enrollmentRequestRepo.save(request);
    }

    /**
     * Aprueba una solicitud de inscripción pendiente.
     * Al aprobar:
     * 1. Si el usuario era ASPIRANTE, asciende a USUARIO.
     * 2. Se crea la relación usuario-rol-club en la tabla usuarios_roles_clubes.
     * 3. La solicitud queda marcada como APROBADA con fecha de resolución.
     *
     * @param enrollmentRequestId ID de la solicitud a aprobar.
     * @param roleId              ID del rol (de la tabla roles) que se le asignará al nuevo miembro.
     */
    @Transactional
    public void approveEnrollment(Long enrollmentRequestId, Long roleId) {
        if (enrollmentRequestId == null) {
            throw new IllegalArgumentException("El ID de la solicitud no puede ser nulo");
        }

        // Buscar la solicitud en la base de datos
        EnrollmentRequest request = enrollmentRequestRepo.findById(enrollmentRequestId)
                .orElseThrow(() -> new EntityNotFoundException("Solicitud no encontrada con ID: " + enrollmentRequestId));

        // Si no se especificó un rol, asignar MIEMBRO por defecto
        ClubRole role;
        if (roleId != null) {
            role = clubRoleRepo.findById(roleId)
                    .orElseThrow(() -> new EntityNotFoundException("El Rol con ID " + roleId + " no existe"));
        } else {
            role = clubRoleRepo.findByName("MIEMBRO")
                    .orElseThrow(() -> new EntityNotFoundException("El rol MIEMBRO no existe en el catálogo"));
        }

        // Se pueden aprobar solicitudes PENDIENTES o en REINTENTO
        if (request.getStatus() != RequestStatus.PENDIENTE
                && request.getStatus() != RequestStatus.REINTENTO) {
            throw new IllegalStateException("Solo se pueden aprobar solicitudes PENDIENTES o en REINTENTO");
        }

        User user = request.getUser();
        Club club = request.getClub();

        // Verificar que el usuario no sea ya miembro activo del club
        boolean alreadyMember = userClubRoleRepo
                .existsByUserIdAndClubIdAndActiveTrue(user.getId(), club.getId());
        if (alreadyMember) {
            throw new IllegalStateException("El usuario ya pertenece a este club");
        }

        // Si el usuario aún era ASPIRANTE, ascenderlo a USUARIO del sistema
        if (user.getSystemRole() == SystemRole.ASPIRANTE) {
            user.setSystemRole(SystemRole.USUARIO);
            userRepo.save(user);
            logger.info("AUDITORÍA - ASCENSO: {} pasó de ASPIRANTE a USUARIO (club: {})",
                    user.getEmail(), club.getName());
        }

        // Crear la relación en la tabla usuarios_roles_clubes
        UserClubRole membership = new UserClubRole();
        membership.setUser(user);
        membership.setClub(club);
        membership.setClubRole(role);
        membership.setActive(true);
        userClubRoleRepo.save(membership);

        // Marcar la solicitud como aprobada
        request.setStatus(RequestStatus.APROBADA);
        request.setResolutionDate(LocalDate.now());
        enrollmentRequestRepo.save(request);

        // Notificar al usuario que su solicitud fue aprobada
        notificationService.notify(
                user,
                NotificationType.APPLICATION_APPROVED,
                "¡Solicitud aprobada!",
                "Tu solicitud para unirte al club \"" + club.getName() + "\" fue aprobada. Ya eres miembro.",
                request.getId());

        logger.info("INSCRIPCIÓN APROBADA: Solicitud {} → usuario {} en club {}",
                enrollmentRequestId, user.getEmail(), club.getName());
    }

    /**
     * Rechaza una solicitud de inscripción pendiente.
     * El campo rejectionReason se guarda en la solicitud para informar al aspirante.
     *
     * @param enrollmentRequestId ID de la solicitud a rechazar.
     * @param reason              Razón del rechazo (obligatorio, no puede estar vacío).
     */
    @Transactional
    public void rejectEnrollment(Long enrollmentRequestId, String reason) {
        if (reason == null || reason.trim().isEmpty()) {
            throw new IllegalArgumentException("El motivo de rechazo es obligatorio");
        }

        EnrollmentRequest request = enrollmentRequestRepo.findById(enrollmentRequestId)
                .orElseThrow(() -> new EntityNotFoundException("Solicitud no encontrada con ID: " + enrollmentRequestId));

        // Se pueden rechazar solicitudes PENDIENTES o en REINTENTO
        if (request.getStatus() != RequestStatus.PENDIENTE
                && request.getStatus() != RequestStatus.REINTENTO) {
            throw new IllegalStateException("Solo se pueden rechazar solicitudes PENDIENTES o en REINTENTO");
        }

        request.setStatus(RequestStatus.RECHAZADA);
        request.setRejectionReason(reason);
        request.setResolutionDate(LocalDate.now());
        enrollmentRequestRepo.save(request);

        // Notificar al usuario que su solicitud fue rechazada
        notificationService.notify(
                request.getUser(),
                NotificationType.APPLICATION_REJECTED,
                "Solicitud rechazada",
                "Tu solicitud para el club \"" + request.getClub().getName()
                        + "\" fue rechazada. Motivo: " + reason,
                request.getId());

        logger.warn("INSCRIPCIÓN RECHAZADA: Solicitud {} del usuario {} — Motivo: {}",
                enrollmentRequestId, request.getUser().getEmail(), reason);
    }

    /**
     * Crea un usuario Aspirante y su solicitud de inscripción en un solo paso.
     * Usado en el formulario público donde alguien que NO tiene cuenta aún puede
     * solicitar unirse a un club.
     *
     * Si el email ya existe en el sistema, se usa ese usuario existente.
     * El comprobante de pago (si se adjunta) se sube a AWS S3.
     *
     * @param name   Nombre completo del aspirante.
     * @param email  Email del aspirante (será su usuario de login).
     * @param clubId Club al que quiere inscribirse.
     * @param file   Comprobante de pago (opcional).
     */
    @Transactional
    public void createApplicantAndEnrollment(String name, String email, Long clubId, MultipartFile file) {
        // 1. Verificar existencia del club
        Club club = clubRepo.findById(clubId)
                .orElseThrow(() -> new EntityNotFoundException("El club seleccionado no existe"));

        // 2. Buscar usuario existente o crear uno nuevo con rol ASPIRANTE
        User user = userRepo.findByEmail(email).orElseGet(() -> {
            User newUser = new User();
            newUser.setFullName(name);
            newUser.setEmail(email);
            newUser.setSystemRole(SystemRole.ASPIRANTE);
            // Contraseña temporal hasheada; el sistema puede pedir cambio posterior
            newUser.setPassword(passwordEncoder.encode(temporaryPassword));
            return userRepo.save(newUser);
        });

        // 3. Validar que no exista ya una solicitud activa (PENDIENTE o REINTENTO) para este club
        boolean exists = enrollmentRequestRepo.existsByUserIdAndClubIdAndStatusIn(
                user.getId(), club.getId(), ACTIVE_STATUSES);
        if (exists) {
            throw new IllegalStateException("Ya existe una solicitud en proceso para el club " + club.getName());
        }

        // 4. Construir la solicitud
        EnrollmentRequest request = new EnrollmentRequest();
        request.setUser(user);
        request.setClub(club);
        request.setStatus(RequestStatus.PENDIENTE);
        request.setRequestDate(LocalDate.now());

        // 5. Subir el comprobante a S3 si fue adjuntado
        if (file != null && !file.isEmpty()) {
            try {
                // Crear archivo temporal en disco para enviarlo al S3Service
                File tempFile = File.createTempFile("comprobante_", "_" + file.getOriginalFilename());
                file.transferTo(tempFile);

                // S3Service sube el archivo y retorna la URL (y borra el temp en su finally)
                String url = s3Service.uploadFile(tempFile);
                request.setPaymentReceipt(url);

            } catch (Exception e) {
                logger.error("Error al procesar el archivo para S3", e);
                throw new RuntimeException("Error en el almacenamiento del archivo: " + e.getMessage());
            }
        } else {
            request.setPaymentReceipt("Sin comprobante");
        }

        enrollmentRequestRepo.save(request);
        logger.info("SOLICITUD CREADA: {} → club {}", email, club.getName());
    }

    /**
     * Crea una solicitud de inscripción para el usuario autenticado, con
     * comprobante de pago opcional. Punto de entrada del nuevo flujo
     * (usuario registrado → aplica a club).
     *
     * @param clubId ID del club al que quiere unirse.
     * @param file   Comprobante de pago (opcional).
     * @return La solicitud creada.
     */
    @Transactional
    public EnrollmentRequest applyToClub(Long clubId, MultipartFile file) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Usuario autenticado no encontrado"));

        Club club = clubRepo.findById(clubId)
                .orElseThrow(() -> new EntityNotFoundException("Club no encontrado con ID: " + clubId));

        // Bloquear si ya hay una solicitud activa (PENDIENTE o REINTENTO)
        boolean exists = enrollmentRequestRepo.existsByUserIdAndClubIdAndStatusIn(
                user.getId(), club.getId(), ACTIVE_STATUSES);
        if (exists) {
            throw new IllegalStateException("Ya tienes una solicitud activa para este club");
        }

        EnrollmentRequest request = new EnrollmentRequest();
        request.setUser(user);
        request.setClub(club);
        request.setStatus(RequestStatus.PENDIENTE);
        request.setRequestDate(LocalDate.now());

        if (file != null && !file.isEmpty()) {
            try {
                File tempFile = File.createTempFile("comprobante_", "_" + file.getOriginalFilename());
                file.transferTo(tempFile);
                String url = s3Service.uploadFile(tempFile);
                request.setPaymentReceipt(url);
            } catch (Exception e) {
                logger.error("Error al subir comprobante a S3", e);
                throw new RuntimeException("Error al almacenar el comprobante: " + e.getMessage());
            }
        }

        EnrollmentRequest saved = enrollmentRequestRepo.save(request);
        logger.info("SOLICITUD CREADA (nuevo flujo): {} → club {}", email, club.getName());
        return saved;
    }

    /**
     * Permite a un aspirante reintentar su solicitud después de un rechazo.
     * Reutiliza el registro existente: actualiza el comprobante, cambia el estado
     * a REINTENTO, limpia el motivo de rechazo e incrementa la versión.
     *
     * Reglas:
     *   - Solo el propietario de la solicitud puede reintentarla.
     *   - La solicitud debe estar en estado RECHAZADA.
     *   - Se debe adjuntar un nuevo comprobante de pago.
     *
     * @param applicationId ID de la solicitud RECHAZADA.
     * @param file          Nuevo comprobante de pago (obligatorio).
     * @return La solicitud actualizada como DTO.
     */
    @Transactional
    public ApplicationDTO resubmitApplication(Long applicationId, MultipartFile file) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User currentUser = userRepo.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Usuario autenticado no encontrado"));

        EnrollmentRequest request = enrollmentRequestRepo.findById(applicationId)
                .orElseThrow(() -> new EntityNotFoundException("Solicitud no encontrada con ID: " + applicationId));

        // Verificar propiedad
        if (!request.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("No tienes permiso para reintentar esta solicitud");
        }

        // Solo se pueden reintentar solicitudes RECHAZADAS
        if (request.getStatus() != RequestStatus.RECHAZADA) {
            throw new IllegalStateException("Solo se pueden reintentar solicitudes en estado RECHAZADA");
        }

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Debes adjuntar un nuevo comprobante de pago para reintentar");
        }

        // Subir nuevo comprobante a S3
        try {
            File tempFile = File.createTempFile("reintento_", "_" + file.getOriginalFilename());
            file.transferTo(tempFile);
            String url = s3Service.uploadFile(tempFile);
            request.setPaymentReceipt(url);
        } catch (Exception e) {
            logger.error("Error al subir nuevo comprobante a S3 (reintento)", e);
            throw new RuntimeException("Error al almacenar el comprobante: " + e.getMessage());
        }

        // Actualizar estado del registro (no se crea uno nuevo)
        request.setStatus(RequestStatus.REINTENTO);
        request.setResubmittedAt(LocalDateTime.now());
        request.setVersion(request.getVersion() + 1);
        request.setRejectionReason(null);    // Limpiar motivo anterior
        request.setResolutionDate(null);     // Limpiar fecha de resolución anterior

        EnrollmentRequest saved = enrollmentRequestRepo.save(request);
        logger.info("REINTENTO: Solicitud {} del usuario {} para club {}",
                applicationId, email, request.getClub().getName());

        // Construir y retornar DTO
        return toApplicationDTO(saved);
    }

    /**
     * Retorna las solicitudes del usuario autenticado como DTOs, ordenadas
     * de más reciente a más antigua.
     *
     * @Transactional(readOnly=true): abre una sesión JPA durante toda la
     * ejecución del método, lo que evita LazyInitializationException al
     * acceder a request.getUser() y request.getClub() dentro del stream.
     * Sin esta anotación, la sesión se cierra al salir del repositorio y
     * cualquier acceso lazy lanzaría una excepción.
     *
     * Devuelve DTO en lugar de la entidad para no exponer datos internos
     * (hash de contraseña, campos de auditoría, etc.) en la respuesta JSON.
     */
    @Transactional(readOnly = true)
    public List<EnrollmentRequestDTO> getMyEnrollmentRequests() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return enrollmentRequestRepo.findByUserEmailOrderByRequestDateDesc(email)
                .stream()
                .map(r -> {
                    EnrollmentRequestDTO dto = new EnrollmentRequestDTO();
                    dto.setId(r.getId());
                    dto.setUserId(r.getUser().getId());
                    dto.setUserName(r.getUser().getFullName());
                    dto.setUserEmail(r.getUser().getEmail());
                    dto.setStatus(r.getStatus());
                    dto.setCreatedAt(r.getRequestDate());
                    dto.setClubId(r.getClub().getId());
                    dto.setClubName(r.getClub().getName());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    /**
     * Retorna las solicitudes del usuario autenticado como ApplicationDTO (nuevo flujo).
     * Incluye rejectionReason, resubmittedAt y version además de los campos base.
     */
    @Transactional(readOnly = true)
    public List<ApplicationDTO> getMyApplications() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Usuario autenticado no encontrado"));
        return enrollmentRequestRepo.findByUserIdOrderByRequestDateDesc(user.getId())
                .stream()
                .map(this::toApplicationDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retorna el detalle de una solicitud como ApplicationDTO.
     * Verifica que pertenezca al usuario autenticado.
     */
    @Transactional(readOnly = true)
    public ApplicationDTO getApplicationById(Long applicationId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Usuario autenticado no encontrado"));

        EnrollmentRequest request = enrollmentRequestRepo.findById(applicationId)
                .orElseThrow(() -> new EntityNotFoundException("Solicitud no encontrada con ID: " + applicationId));

        if (!request.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("No tienes permiso para ver esta solicitud");
        }
        return toApplicationDTO(request);
    }

    // ── helpers ────────────────────────────────────────────────────────────────

    /**
     * Convierte una entidad EnrollmentRequest al ApplicationDTO extendido.
     * Centraliza el mapeo para evitar duplicación en los distintos métodos.
     */
    public ApplicationDTO toApplicationDTO(EnrollmentRequest r) {
        ApplicationDTO dto = new ApplicationDTO();
        dto.setId(r.getId());
        dto.setUserId(r.getUser().getId());
        dto.setUserName(r.getUser().getFullName());
        dto.setUserEmail(r.getUser().getEmail());
        dto.setStatus(r.getStatus());
        dto.setCreatedAt(r.getRequestDate());
        dto.setClubId(r.getClub().getId());
        dto.setClubName(r.getClub().getName());
        dto.setPaymentReceipt(r.getPaymentReceipt());
        dto.setRejectionReason(r.getRejectionReason());
        dto.setResubmittedAt(r.getResubmittedAt());
        dto.setVersion(r.getVersion());
        return dto;
    }
}
