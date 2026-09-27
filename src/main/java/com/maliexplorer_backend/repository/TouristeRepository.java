package com.maliexplorer_backend.Repository;

import com.maliexplorer_backend.Models.TouristeModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TouristeRepository extends JpaRepository<TouristeModel, Integer> {
    Optional<TouristeModel> findByEmail(String email);

    boolean existsByEmail(String email);
}
