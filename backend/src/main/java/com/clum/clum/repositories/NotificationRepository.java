package com.clum.clum.repositories;

import com.clum.clum.models.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    /** Notificaciones de un usuario ordenadas por fecha descendente (paginadas) */
    Page<Notification> findByUserIdOrderByFechaCreacionDesc(Long userId, Pageable pageable);

    /** Notificaciones no leídas de un usuario (para el dropdown de la campana) */
    Page<Notification> findByUserIdAndLeidaFalseOrderByFechaCreacionDesc(Long userId, Pageable pageable);

    /** Conteo de notificaciones no leídas (para el badge de la campana) */
    long countByUserIdAndLeidaFalse(Long userId);
}
