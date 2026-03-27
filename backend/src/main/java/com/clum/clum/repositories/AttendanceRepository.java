package com.clum.clum.repositories;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.clum.clum.models.Attendance;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    /** Todas las asistencias de un evento */
    List<Attendance> findByEventId(Long eventId);

    /** Todas las asistencias confirmadas de un evento */
    List<Attendance> findByEventIdAndConfirmedTrue(Long eventId);

    /**
     * Saber si ya hay un registro de asistencia para este usuario en este evento
     */
    Optional<Attendance> findByEventIdAndUserId(Long eventId, Long userId);

    /** Historial de asistencia de un usuario */
    List<Attendance> findByUserEmailOrderByEventEventDateDesc(String email);

    /** Contar cuántos asistieron realmente */
    long countByEventIdAndAttendedTrue(Long eventId);
}
