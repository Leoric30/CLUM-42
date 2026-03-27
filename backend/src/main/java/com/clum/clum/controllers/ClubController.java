package com.clum.clum.controllers;

import com.clum.clum.models.Club;
import com.clum.clum.repositories.ClubRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controlador REST para consultas públicas de clubes.
 * Base URL: /api/clubs
 */
@RestController
@RequestMapping("/api/clubs")
public class ClubController {

    private final ClubRepository clubRepository;

    public ClubController(ClubRepository clubRepository) {
        this.clubRepository = clubRepository;
    }

    /**
     * Devuelve la lista de clubes activos.
     * Endpoint público — usado en el formulario de inscripción para
     * poblar el selector de club sin requerir autenticación.
     *
     * GET /api/clubs
     *
     * @return lista de objetos { id, name, description }
     */
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getActiveClubs() {
        List<Map<String, Object>> clubs = clubRepository.findAll()
                .stream()
                .filter(Club::isActive)
                .map(c -> Map.<String, Object>of(
                        "id",          c.getId(),
                        "name",        c.getName(),
                        "description", c.getDescription() != null ? c.getDescription() : ""
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(clubs);
    }
}
