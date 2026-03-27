package com.clum.clum.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.clum.clum.dto.ApproveEnrollmentRequest;
import com.clum.clum.dto.RejectEnrollmentRequest;
import com.clum.clum.dto.EnrollmentRequestDTO;
import com.clum.clum.services.EnrollmentService;

import jakarta.validation.Valid;

/**
 * Controlador REST para el flujo de inscripción de miembros.
 * Base URL: /api/enrollments
 *
 * Expone endpoints para:
 * - Enviar solicitud pública (formulario sin login)
 * - Aprobar/rechazar solicitudes (requiere ser autoridad del club)
 * - Consultar las propias solicitudes (usuario autenticado)
 */
@RestController
@RequestMapping("/api/enrollments")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    /**
     * Aprueba la solicitud de inscripción de un aspirante.
     * Solo accesible para el Director o Secretario del club indicado.
     *
     * POST /api/enrollments/clubs/{clubId}/requests/{enrollmentRequestId}/approve
     *
     * @param clubId              ID del club (usado por @PreAuthorize para verificar autoridad)
     * @param enrollmentRequestId ID de la solicitud a aprobar
     * @param request             Body con el roleId que se le asignará al nuevo miembro
     */
    @PreAuthorize("@clubSecurity.isClubAuthority(#clubId)")
    @PostMapping("/clubs/{clubId}/requests/{enrollmentRequestId}/approve")
    public ResponseEntity<?> approveEnrollment(
            @PathVariable Long clubId,
            @PathVariable Long enrollmentRequestId,
            @Valid @RequestBody ApproveEnrollmentRequest request) {
        enrollmentService.approveEnrollment(enrollmentRequestId, request.getRoleId());
        return ResponseEntity.ok("Inscripción aprobada correctamente");
    }

    /**
     * Rechaza la solicitud de inscripción de un aspirante.
     * Solo accesible para el Director o Secretario del club indicado.
     *
     * POST /api/enrollments/clubs/{clubId}/requests/{enrollmentRequestId}/reject
     *
     * @param clubId              ID del club (usado por @PreAuthorize)
     * @param enrollmentRequestId ID de la solicitud a rechazar
     * @param request             Body con el motivo del rechazo
     */
    @PreAuthorize("@clubSecurity.isClubAuthority(#clubId)")
    @PostMapping("/clubs/{clubId}/requests/{enrollmentRequestId}/reject")
    public ResponseEntity<?> rejectEnrollment(
            @PathVariable Long clubId,
            @PathVariable Long enrollmentRequestId,
            @Valid @RequestBody RejectEnrollmentRequest request) {
        enrollmentService.rejectEnrollment(enrollmentRequestId, request.getReason());
        return ResponseEntity.ok("Inscripción rechazada correctamente");
    }

    /**
     * Endpoint público para enviar una solicitud de inscripción (formulario de
     * aspirante).
     * No requiere autenticación. Acepta multipart/form-data para recibir el
     * comprobante.
     *
     * POST /api/enrollments/register
     */
    @PostMapping(value = "/register", consumes = { "multipart/form-data" })
    public ResponseEntity<?> submitEnrollment(
            @RequestParam("name") String name,
            @RequestParam("email") String email,
            @RequestParam("clubId") Long clubId,
            @RequestParam("file") org.springframework.web.multipart.MultipartFile file) {
        enrollmentService.createApplicantAndEnrollment(name, email, clubId, file);
        return ResponseEntity.ok("Solicitud enviada exitosamente");
    }

    /**
     * Retorna las solicitudes del usuario autenticado como DTOs.
     * Al devolver EnrollmentRequestDTO en lugar de la entidad completa,
     * se evita serializar el hash BCrypt de la contraseña y otros campos
     * internos que no son relevantes para el cliente.
     *
     * GET /api/enrollments/my-requests
     */
    @GetMapping("/my-requests")
    public ResponseEntity<List<EnrollmentRequestDTO>> getMyEnrollmentRequests() {
        return ResponseEntity.ok(enrollmentService.getMyEnrollmentRequests());
    }
}
