package com.clum.clum.repositories;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.clum.clum.models.Announcement;

@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {

    /** Comunicados activos de un club específico, paginados */
    Page<Announcement> findByClubIdAndActiveTrueOrderByPublishedAtDesc(Long clubId, Pageable pageable);

    /** Comunicados globales (sin club asignado), activos */
    List<Announcement> findByClubIsNullAndActiveTrueOrderByPublishedAtDesc();

    /**
     * Comunicados del club + globales combinados (para la vista general del
     * miembro)
     */
    List<Announcement> findByClubIdOrClubIsNullAndActiveTrueOrderByPublishedAtDesc(Long clubId);

    /** Comunicados publicados por un autor específico */
    List<Announcement> findByAuthorEmailAndActiveTrue(String email);
}
