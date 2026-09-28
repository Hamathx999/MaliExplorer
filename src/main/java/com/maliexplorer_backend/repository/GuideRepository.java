package com.maliexplorer_backend.Repository;

import com.maliexplorer_backend.model.Guide;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GuideRepository extends JpaRepository<Guide, Integer> {
    Optional<Guide> findByEmail(String email);

    boolean existsByEmail(String email);

    List<Guide> findByLangueContainingIgnoreCase(String langue);
}
