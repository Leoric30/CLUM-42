package com.clum.clum.controllers;

import com.clum.clum.dto.ApplicationDTO;
import com.clum.clum.dto.ApproveEnrollmentRequest;
import com.clum.clum.dto.RejectEnrollmentRequest;
import com.clum.clum.models.EnrollmentRequest;
import com.clum.clum.models.enums.RequestStatus;
import com.clum.clum.services.EnrollmentRequestService;
import com.clum.clum.services.EnrollmentService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Controller REST del nuevo flujo de solicitudes de inscripción.
 * Base URL: /api/applications
 *
 * Expone los endpoints del plan "usuario primero":
 *   - POST   /api/applications              → usuario autenticado aplica a un club
 *   - GET    /api/applications/mine         → mis solicitudes
 *   - GET    /api/applications/{id}         → detalle de una solicitud (solo propietario)
 *   - PUT    /api/applications/{id}/resubmit → reintento tras rechazo
 *   - GET    /api/applications/club/{clubId} → solicitudes activas del club (director/admin)
 *   - PATCH  /api/applications/{id}/approve → aprobar
 *   - PATCH  /api/applications/{id}/reject  → rechazar
 *
 * Los endpoints legacy /api/enrollments/* se mantienen sin cambios.
 */
@RestController
@RequestMapping("/api/applications")
public class ApplicationController {

    private final EnrollmentService enrollmentService;
    private final EnrollmentRequestService enrollmentRequestService;

    public ApplicationController(EnrollmentService enrollmentService,
                                  EnrollmentRequestService enrollmentRequestService) {
        this.enrollmentService = enrollmentService;
        this.enrollmentRequestService = enrollmentRequestService;
    }

    /**
     * Aplica a un club.
     * El usuario autenticado envía su solicitud con un comprobante de pago opcional.
     *
     * POST /api/applications
     * Content-Type: multipart/form-data
     * Params: clubId (Long), paymentProof (MultipartFile, opcional)
     */
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<?> apply(
            @RequestParam Long clubId,
            @RequestParam(required = false) MultipartFile paymentProof) {
        try {
            EnrollmentRequest created = enrollmentService.applyToClub(clubId, paymentProof);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(enrollmentService.toApplicationDTO(created));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(java.util.Map.of("error", e.getMessage()));
        }
    }

    /**
     * Retorna todas las solicitudes del usuario autenticado.
     *
     * GET /api/applications/mine
     */
    @GetMapping("/mine")
    public ResponseEntity<List<ApplicationDTO>> mine() {
        return ResponseEntity.ok(enrollmentService.getMyApplications());
    }

    /**
     * Detalle de una solicitud específica (solo el propietario puede verla).
     *
     * GET /api/applications/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(enrollmentService.getApplicationById(id));
        } catch (jakarta.persistence.EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (org.springframework.security.access.AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(java.util.Map.of("error", e.getMessage()));
        }
    }

    /**
     * Reintento: el usuario actualiza su comprobante tras un rechazo.
     * Solo funciona si el estado actual es RECHAZADA.
     *
     * PUT /api/applications/{id}/resubmit
     * Content-Type: multipart/form-data
     * Params: paymentProof (MultipartFile, obligatorio)
     */
    @PutMapping(value = "/{id}/resubmit", consumes = "multipart/form-data")
    public ResponseEntity<?> resubmit(
            @PathVariable Long id,
            @RequestParam MultipartFile paymentProof) {
        try {
            ApplicationDTO dto = enrollmentService.resubmitApplication(id, paymentProof);
            return ResponseEntity.ok(dto);
        } catch (IllegalStateException | IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(java.util.Map.of("error", e.getMessage()));
        } catch (org.springframework.security.access.AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(java.util.Map.of("error", e.getMessage()));
        }
    }

    /**
     * Solicitudes activas (PENDIENTE + REINTENTO) de un club para el director.
     * Soporta filtro opcional por estado: ?status=RECHAZADA, ?status=APROBADA, etc.
     *
     * GET /api/applications/club/{clubId}
     * GET /api/applications/club/{clubId}?status=RECHAZADA
     */
    @GetMapping("/club/{clubId}")
    @PreAuthorize("@clubSecurity.isClubAuthority(#clubId)")
    public ResponseEntity<Page<ApplicationDTO>> byClub(
            @PathVariable Long clubId,
            @RequestParam(required = false) RequestStatus status,
            Pageable pageable) {
        if (status != null) {
            return ResponseEntity.ok(enrollmentRequestService.listByClubAndStatus(clubId, status, pageable));
        }
        return ResponseEntity.ok(enrollmentRequestService.listActiveByClub(clubId, pageable));
    }

    /**
     * Aprueba una solicitud. Opcionalmente asigna un rol específico al nuevo miembro.
     * Si roleId es null, se asigna el rol MIEMBRO por defecto.
     *
     * PATCH /api/applications/{id}/approve
     * Body: { "roleId": <Long o null> }
     */
    @PatchMapping("/{id}/approve")
    @PreAuthorize("@clubSecurity.isClubAuthorityByApplicationId(#id)")
    public ResponseEntity<?> approve(
            @PathVariable Long id,
            @RequestBody(required = false) ApproveEnrollmentRequest body) {
        try {
            Long roleId = (body != null) ? body.getRoleId() : null;
            enrollmentService.approveEnrollment(id, roleId);
            return ResponseEntity.ok(java.util.Map.of("message", "Solicitud aprobada correctamente"));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest()
                    .body(java.util.Map.of("error", e.getMessage()));
        }
    }

    /**
     * Rechaza una solicitud. El motivo es obligatorio.
     *
     * PATCH /api/applications/{id}/reject
     * Body: { "reason": "..." }
     */
    @PatchMapping("/{id}/reject")
    @PreAuthorize("@clubSecurity.isClubAuthorityByApplicationId(#id)")
    public ResponseEntity<?> reject(
            @PathVariable Long id,
            @Valid @RequestBody RejectEnrollmentRequest body) {
        try {
            enrollmentService.rejectEnrollment(id, body.getReason());
            return ResponseEntity.ok(java.util.Map.of("message", "Solicitud rechazada correctamente"));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest()
                    .body(java.util.Map.of("error", e.getMessage()));
        }
    }
}
