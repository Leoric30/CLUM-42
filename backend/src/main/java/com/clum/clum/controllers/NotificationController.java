package com.clum.clum.controllers;

import com.clum.clum.models.Notification;
import com.clum.clum.services.NotificationService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller REST para notificaciones in-app.
 * Base URL: /api/notifications
 *
 * Todos los endpoints requieren autenticación y operan sobre las
 * notificaciones del usuario en sesión.
 */
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /**
     * Lista las notificaciones del usuario autenticado, paginadas.
     *
     * GET /api/notifications
     * GET /api/notifications?unreadOnly=true   → solo no leídas
     */
    @GetMapping
    public ResponseEntity<Page<Notification>> list(
            @RequestParam(defaultValue = "false") boolean unreadOnly,
            Pageable pageable) {
        return ResponseEntity.ok(notificationService.getMyNotifications(unreadOnly, pageable));
    }

    /**
     * Retorna el número de notificaciones no leídas del usuario autenticado.
     * Usado por el badge de la campana (polling cada 60 s desde el frontend).
     *
     * GET /api/notifications/unread-count
     * Respuesta: { "count": N }
     */
    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Long>> unreadCount() {
        return ResponseEntity.ok(Map.of("count", notificationService.getUnreadCount()));
    }

    /**
     * Marca una notificación específica como leída.
     *
     * PUT /api/notifications/{id}/read
     */
    @PutMapping("/{id}/read")
    public ResponseEntity<?> markAsRead(@PathVariable Long id) {
        try {
            notificationService.markAsRead(id);
            return ResponseEntity.ok(Map.of("read", true));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(403).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Marca todas las notificaciones del usuario autenticado como leídas.
     * Limpia el badge de la campana de una sola vez.
     *
     * PUT /api/notifications/read-all
     */
    @PutMapping("/read-all")
    public ResponseEntity<?> markAllAsRead() {
        notificationService.markAllAsRead();
        return ResponseEntity.ok(Map.of("read", true));
    }
}
