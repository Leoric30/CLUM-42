package com.clum.clum.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.clum.clum.services.EnrollmentRequestService;
import com.clum.clum.dto.EnrollmentRequestDTO;

/**
 * Controlador REST para consultar solicitudes de inscripción.
 * Base URL: /api/clubs
 *
 * Complementa a EnrollmentController — este se encarga solo de lectura
 * paginada.
 * Destinado a la vista del secretario/director en el frontend React.
 */
@RestController
@RequestMapping("/api/clubs")
public class EnrollmentRequestController {

    private final EnrollmentRequestService enrollmentRequestService;

    public EnrollmentRequestController(EnrollmentRequestService enrollmentRequestService) {
        this.enrollmentRequestService = enrollmentRequestService;
    }

    /**
     * Retorna de forma paginada las solicitudes PENDIENTES de un club.
     * Soporta parámetros de Spring Data Pageable:
     * ?page=0&size=10&sort=requestDate,desc
     *
     * GET /api/clubs/{clubId}/requests/pending
     *
     * @PreAuthorize garantiza que solo el Director o Secretario del club
     * con ese clubId pueda ver sus solicitudes. Sin esta anotación,
     * cualquier usuario autenticado puede pasar un clubId arbitrario
     * en la URL y ver solicitudes ajenas.
     *
     * @param clubId   ID del club a consultar.
     * @param pageable Parámetros de paginación y ordenamiento.
     */
    @PreAuthorize("@clubSecurity.isClubAuthority(#clubId)")
    @GetMapping("/{clubId}/requests/pending")
    public ResponseEntity<Page<EnrollmentRequestDTO>> listPending(
            @PathVariable Long clubId,
            Pageable pageable) {
        return ResponseEntity.ok(
                enrollmentRequestService.listPendingByClub(clubId, pageable));
    }
}
