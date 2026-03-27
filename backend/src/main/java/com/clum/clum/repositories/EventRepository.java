package com.clum.clum.repositories;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.clum.clum.models.Event;
import com.clum.clum.models.enums.EventType;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    /** Todos los eventos activos de un club, ordenados por fecha */
    List<Event> findByClubIdAndActiveTrueOrderByEventDateAsc(Long clubId);

    /** Eventos de un club filtrados por tipo (JUNTA, ACTIVIDAD, etc.) */
    List<Event> findByClubIdAndTypeAndActiveTrue(Long clubId, EventType type);

    /** Eventos a partir de cierta fecha (para vista de calendario) */
    List<Event> findByClubIdAndEventDateAfterAndActiveTrue(Long clubId, LocalDateTime from);

    /** Eventos creados por un usuario específico */
    List<Event> findByCreatedByEmailAndActiveTrue(String email);
}
