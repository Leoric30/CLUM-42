package com.clum.clum.repositories;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.clum.clum.models.AuditLog;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    /** Bitácora completa de acciones de un usuario, paginada */
    Page<AuditLog> findByUserEmailOrderByActionDateDesc(String email, Pageable pageable);

    /**
     * Acciones sobre una entidad concreta (ej: todas las acciones sobre
     * EnrollmentRequest ID 5)
     */
    List<AuditLog> findByEntityAndEntityIdOrderByActionDateDesc(String entity, Long entityId);

    /** Acciones de un tipo específico en un rango de fechas */
    List<AuditLog> findByActionAndActionDateBetweenOrderByActionDateDesc(
            String action,
            LocalDateTime from,
            LocalDateTime to);
}
