package com.clum.clum.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.clum.clum.models.Fee;

@Repository
public interface FeeRepository extends JpaRepository<Fee, Long> {

    /** Cuotas activas de un club */
    List<Fee> findByClubIdAndActiveTrue(Long clubId);

    /** Todas las cuotas de un club (incluyendo inactivas, para historial) */
    List<Fee> findByClubIdOrderByDueDateDesc(Long clubId);
}
