package com.clum.clum.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.clum.clum.models.EnrollmentRequest;
import com.clum.clum.models.enums.RequestStatus;

/**
 * Repositorio JPA para la tabla solicitudes_inscripcion.
 * Spring Data JPA genera automáticamente las consultas a partir del nombre del
 * método.
 */
@Repository
public interface EnrollmentRequestRepository
                extends JpaRepository<EnrollmentRequest, Long> {

        /** Todas las solicitudes de un club (cualquier estado) */
        List<EnrollmentRequest> findByClubId(Long clubId);

        /** Solicitudes filtradas por estado (PENDIENTE, APROBADA, RECHAZADA) */
        List<EnrollmentRequest> findByStatus(RequestStatus status);

        /** Solicitudes de un club filtradas por estado (sin paginación) */
        List<EnrollmentRequest> findByClubIdAndStatus(Long clubId, RequestStatus status);

        /**
         * Solicitudes de un usuario ordenadas por fecha descendente (para "Mis
         * Solicitudes"). JOIN FETCH evita N+1 al mapear user y club en el servicio.
         */
        @EntityGraph(attributePaths = {"user", "club"})
        List<EnrollmentRequest> findByUserEmailOrderByRequestDateDesc(String email);

        /**
         * Solicitudes paginadas de un club filtradas por estado.
         * JOIN FETCH de user evita N+1 al construir el DTO en el servicio.
         */
        @EntityGraph(attributePaths = {"user"})
        Page<EnrollmentRequest> findByClubIdAndStatus(Long clubId, RequestStatus status, Pageable pageable);

        /**
         * Verifica si ya existe una solicitud activa del usuario para el club en un
         * estado dado
         */
        boolean existsByUserIdAndClubIdAndStatus(Long userId, Long clubId, RequestStatus status);

        /**
         * Verifica si existe una solicitud del usuario en cualquiera de los estados
         * indicados (útil para comprobar si hay una solicitud "activa":
         * PENDIENTE o REINTENTO).
         */
        boolean existsByUserIdAndClubIdAndStatusIn(Long userId, Long clubId, List<RequestStatus> statuses);

        /**
         * Busca la solicitud más reciente de un usuario para un club específico,
         * independientemente del estado. Usado en el flujo de reintento para
         * recuperar el registro a actualizar.
         */
        @EntityGraph(attributePaths = {"user", "club"})
        Optional<EnrollmentRequest> findTopByUserIdAndClubIdOrderByRequestDateDesc(Long userId, Long clubId);

        /**
         * Solicitudes de un club con múltiples estados posibles, paginadas.
         * Permite que el director vea tanto PENDIENTE como REINTENTO en su panel.
         */
        @EntityGraph(attributePaths = {"user"})
        Page<EnrollmentRequest> findByClubIdAndStatusIn(Long clubId, List<RequestStatus> statuses, Pageable pageable);

        /**
         * Solicitudes de un club con múltiples estados posibles, sin paginación.
         */
        List<EnrollmentRequest> findByClubIdAndStatusIn(Long clubId, List<RequestStatus> statuses);

        /**
         * Todas las solicitudes del usuario con JOIN FETCH, ordenadas por fecha desc.
         * Usado en GET /api/applications/mine.
         */
        @EntityGraph(attributePaths = {"user", "club"})
        List<EnrollmentRequest> findByUserIdOrderByRequestDateDesc(Long userId);
}
