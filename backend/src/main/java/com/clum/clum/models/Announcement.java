package com.clum.clum.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

/**
 * Tabla: comunicados
 * Avisos formales publicados por autoridades hacia los miembros.
 * Si club es null, el comunicado es global (toda la organización).
 */
@Entity
@Table(name = "comunicados")
@Getter
@Setter
@NoArgsConstructor
public class Announcement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "titulo", nullable = false)
    private String title;

    @Column(name = "contenido", columnDefinition = "TEXT")
    private String content;

    /**
     * Club al que va dirigido el comunicado.
     * Si es null, el comunicado es para toda la organización.
     */
    @ManyToOne
    @JoinColumn(name = "club_id")
    private Club club;

    /** Usuario que redactó y publicó el comunicado */
    @ManyToOne(optional = false)
    @JoinColumn(name = "autor_id")
    private User author;

    @Column(name = "fecha_publicacion", nullable = false)
    private LocalDateTime publishedAt;

    @Column(name = "activo", nullable = false)
    private boolean active = true;

    @PrePersist
    public void prePersist() {
        if (this.publishedAt == null) {
            this.publishedAt = LocalDateTime.now();
        }
    }
}
