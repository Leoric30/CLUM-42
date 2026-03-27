package com.clum.clum.repositories;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.clum.clum.models.Club;

@Repository
public interface ClubRepository
                extends JpaRepository<Club, Long> {
    Optional<Club> findByName(String name);
}
