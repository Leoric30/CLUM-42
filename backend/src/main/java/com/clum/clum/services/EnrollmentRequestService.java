package com.clum.clum.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.clum.clum.dto.ApplicationDTO;
import com.clum.clum.repositories.EnrollmentRequestRepository;
import com.clum.clum.dto.EnrollmentRequestDTO;
import com.clum.clum.models.EnrollmentRequest;
import com.clum.clum.models.enums.RequestStatus;

import java.util.Arrays;

/**
 * Servicio de consulta de solicitudes de inscripción.
 * Separa la lógica de LECTURA de las solicitudes del flujo de
 * aprobación/rechazo
 * (que está en EnrollmentService).
 *
 * Provee versiones paginadas y no paginadas de las consultas,
 * mapeando entidades a DTOs cuando es necesario para el frontend.
 */
@Service
public class EnrollmentRequestService {

    private final EnrollmentRequestRepository enrollmentRequestRepo;

    public EnrollmentRequestService(EnrollmentRequestRepository enrollmentRequestRepo) {
        this.enrollmentRequestRepo = enrollmentRequestRepo;
    }

    // Estados "activos" que el director debe revisar
    private static final List<RequestStatus> ACTIVE_STATUSES =
            Arrays.asList(RequestStatus.PENDIENTE, RequestStatus.REINTENTO);

    /**
     * Retorna todas las solicitudes PENDIENTES de un club (sin paginación).
     * Mantenido para compatibilidad con código existente.
     *
     * @param clubId ID del club a consultar.
     */
    @Transactional(readOnly = true)
    public List<EnrollmentRequest> listPendingByClub(Long clubId) {
        return enrollmentRequestRepo.findByClubIdAndStatus(clubId, RequestStatus.PENDIENTE);
    }

    /**
     * Retorna las solicitudes PENDIENTES de un club en formato paginado y como DTO.
     * Endpoint legacy — mantenido para compatibilidad.
     *
     * @param clubId   ID del club a consultar.
     * @param pageable Información de página y tamaño recibida desde el request.
     */
    @Transactional(readOnly = true)
    public Page<EnrollmentRequestDTO> listPendingByClub(Long clubId, Pageable pageable) {
        return enrollmentRequestRepo
                .findByClubIdAndStatus(clubId, RequestStatus.PENDIENTE, pageable)
                .map(this::toDTO);
    }

    /**
     * Retorna las solicitudes activas (PENDIENTE + REINTENTO) de un club,
     * paginadas y como ApplicationDTO extendido.
     * Usado por el nuevo endpoint GET /api/applications/club/{clubId}.
     *
     * @param clubId   ID del club.
     * @param pageable Paginación.
     */
    @Transactional(readOnly = true)
    public Page<ApplicationDTO> listActiveByClub(Long clubId, Pageable pageable) {
        return enrollmentRequestRepo
                .findByClubIdAndStatusIn(clubId, ACTIVE_STATUSES, pageable)
                .map(this::toApplicationDTO);
    }

    /**
     * Retorna las solicitudes de un club filtradas por estado específico,
     * paginadas como ApplicationDTO.
     * Usado por GET /api/applications/club/{clubId}?status=RECHAZADA etc.
     *
     * @param clubId   ID del club.
     * @param status   Estado a filtrar (cualquier RequestStatus).
     * @param pageable Paginación.
     */
    @Transactional(readOnly = true)
    public Page<ApplicationDTO> listByClubAndStatus(Long clubId, RequestStatus status, Pageable pageable) {
        return enrollmentRequestRepo
                .findByClubIdAndStatus(clubId, status, pageable)
                .map(this::toApplicationDTO);
    }

    /**
     * Convierte una entidad EnrollmentRequest al DTO base (legacy).
     */
    private EnrollmentRequestDTO toDTO(EnrollmentRequest request) {
        EnrollmentRequestDTO dto = new EnrollmentRequestDTO();
        dto.setId(request.getId());
        dto.setUserId(request.getUser().getId());
        dto.setUserName(request.getUser().getFullName());
        dto.setUserEmail(request.getUser().getEmail());
        dto.setStatus(request.getStatus());
        dto.setCreatedAt(request.getRequestDate());
        dto.setClubId(request.getClub().getId());
        dto.setClubName(request.getClub().getName());
        dto.setPaymentReceipt(request.getPaymentReceipt());
        return dto;
    }

    /**
     * Convierte una entidad EnrollmentRequest al ApplicationDTO extendido.
     */
    private ApplicationDTO toApplicationDTO(EnrollmentRequest r) {
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
