package com.clum.clum.services;

import com.clum.clum.models.Notification;
import com.clum.clum.models.User;
import com.clum.clum.models.enums.NotificationType;
import com.clum.clum.repositories.NotificationRepository;
import com.clum.clum.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Servicio de notificaciones in-app.
 * Genera y gestiona notificaciones para los usuarios cuando sus
 * solicitudes de inscripción son aprobadas, rechazadas o reintentadas.
 *
 * El frontend hace polling de GET /api/notifications/unread-count
 * cada 60 s para actualizar el badge de la campana.
 */
@Service
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    private final NotificationRepository notificationRepo;
    private final UserRepository userRepo;

    public NotificationService(NotificationRepository notificationRepo, UserRepository userRepo) {
        this.notificationRepo = notificationRepo;
        this.userRepo = userRepo;
    }

    /**
     * Crea y persiste una notificación para el usuario indicado.
     *
     * @param user      Destinatario de la notificación.
     * @param tipo      Tipo de evento que la origina.
     * @param titulo    Título corto visible en el dropdown.
     * @param mensaje   Cuerpo completo con el contexto del evento.
     * @param entidadId ID del registro relacionado (p.ej. ID de la solicitud).
     */
    @Transactional
    public void notify(User user, NotificationType tipo, String titulo, String mensaje, Long entidadId) {
        Notification n = new Notification();
        n.setUser(user);
        n.setTipo(tipo);
        n.setTitulo(titulo);
        n.setMensaje(mensaje);
        n.setEntidad("EnrollmentRequest");
        n.setEntidadId(entidadId);
        n.setFechaCreacion(LocalDateTime.now());
        notificationRepo.save(n);
        logger.debug("Notificación creada para {}: {}", user.getEmail(), titulo);
    }

    /**
     * Retorna las notificaciones del usuario autenticado, paginadas.
     *
     * @param unreadOnly Si es true, solo devuelve las no leídas.
     * @param pageable   Configuración de paginación.
     */
    @Transactional(readOnly = true)
    public Page<Notification> getMyNotifications(boolean unreadOnly, Pageable pageable) {
        Long userId = getCurrentUserId();
        if (unreadOnly) {
            return notificationRepo.findByUserIdAndLeidaFalseOrderByFechaCreacionDesc(userId, pageable);
        }
        return notificationRepo.findByUserIdOrderByFechaCreacionDesc(userId, pageable);
    }

    /**
     * Retorna el número de notificaciones no leídas del usuario autenticado.
     * Usado por el badge de la campana en el frontend.
     */
    @Transactional(readOnly = true)
    public long getUnreadCount() {
        return notificationRepo.countByUserIdAndLeidaFalse(getCurrentUserId());
    }

    /**
     * Marca una notificación específica como leída.
     * Verifica que pertenezca al usuario autenticado.
     *
     * @param notificationId ID de la notificación a marcar.
     */
    @Transactional
    public void markAsRead(Long notificationId) {
        Notification n = notificationRepo.findById(notificationId)
                .orElseThrow(() -> new EntityNotFoundException("Notificación no encontrada con ID: " + notificationId));

        if (!n.getUser().getId().equals(getCurrentUserId())) {
            throw new AccessDeniedException("No tienes permiso para marcar esta notificación");
        }

        n.setLeida(true);
        n.setFechaLectura(LocalDateTime.now());
        notificationRepo.save(n);
    }

    /**
     * Marca todas las notificaciones del usuario autenticado como leídas.
     * Limpia el badge de la campana de una sola vez.
     */
    @Transactional
    public void markAllAsRead() {
        Long userId = getCurrentUserId();
        Pageable all = PageRequest.of(0, Integer.MAX_VALUE);
        Page<Notification> unread = notificationRepo
                .findByUserIdAndLeidaFalseOrderByFechaCreacionDesc(userId, all);

        LocalDateTime now = LocalDateTime.now();
        unread.forEach(n -> {
            n.setLeida(true);
            n.setFechaLectura(now);
        });
        notificationRepo.saveAll(unread.getContent());
    }

    // ── helpers ────────────────────────────────────────────────────────────────

    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return userRepo.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Usuario autenticado no encontrado"))
                .getId();
    }
}
