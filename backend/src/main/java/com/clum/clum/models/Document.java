package com.clum.clum.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import com.clum.clum.models.enums.DocumentType;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

/**
 * Tabla: documentos
 * Centraliza todos los archivos del sistema (actas, reglamentos, comprobantes,
 * etc.)
 * Reemplaza el patrón de guardar URLs de S3 dispersas en otras entidades.
 *
 * Un documento puede estar asociado opcionalmente a un club y/o a un evento.
 */
@Entity
@Table(name = "documentos")
@Getter
@Setter
@NoArgsConstructor
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Nombre amigable para mostrar en la interfaz */
    @Column(name = "nombre", nullable = false)
    private String name;

    /** URL del archivo en AWS S3 */
    @Column(name = "url", nullable = false)
    private String url;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false)
    private DocumentType type;

    /** Club al que pertenece el documento. Null = documento global */
    @ManyToOne
    @JoinColumn(name = "club_id")
    private Club club;

    /** Usuario que subió el archivo */
    @ManyToOne(optional = false)
    @JoinColumn(name = "subido_por_id")
    private User uploadedBy;

    /**
     * Evento al que está relacionado este documento (ej: acta de una junta).
     * Puede ser null si el documento no corresponde a ningún evento específico.
     */
    @ManyToOne
    @JoinColumn(name = "evento_id")
    private Event event;

    @Column(name = "fecha_subida", nullable = false)
    private LocalDateTime uploadedAt;

    @Column(name = "activo", nullable = false)
    private boolean active = true;

    /**
     * Se ejecuta antes de INSERT y antes de UPDATE.
     *
     * Regla de consistencia club ↔ evento:
     *   Si el documento está asociado a un evento, su club SIEMPRE
     *   debe ser el mismo club al que pertenece ese evento.
     *   Asignarlo aquí evita que la capa de servicio olvide sincronizarlo
     *   y que un documento quede apuntando a dos clubs distintos
     *   (document.club ≠ document.event.club).
     */
    @PrePersist
    @PreUpdate
    public void prePersist() {
        if (this.uploadedAt == null) {
            this.uploadedAt = LocalDateTime.now();
        }
        if (this.event != null) {
            this.club = this.event.getClub();
        }
    }
}
