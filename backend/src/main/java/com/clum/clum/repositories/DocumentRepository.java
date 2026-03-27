package com.clum.clum.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.clum.clum.models.Document;
import com.clum.clum.models.enums.DocumentType;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

    /** Documentos activos de un club, ordenados por fecha descendente */
    List<Document> findByClubIdAndActiveTrueOrderByUploadedAtDesc(Long clubId);

    /** Documentos de un club filtrados por tipo */
    List<Document> findByClubIdAndTypeAndActiveTrue(Long clubId, DocumentType type);

    /** Documentos asociados a un evento (ej: acta de una junta) */
    List<Document> findByEventIdAndActiveTrue(Long eventId);

    /** Documentos globales (sin club) */
    List<Document> findByClubIsNullAndActiveTrueOrderByUploadedAtDesc();
}
