package com.clum.clum.repositories;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.clum.clum.models.ClubRole;

@Repository
public interface ClubRoleRepository
        extends JpaRepository<ClubRole, Long> {

    Optional<ClubRole> findByName(String name);
    Optional<ClubRole> findByDescription(String description);
}
