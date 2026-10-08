package com.maliexplorer_backend.repository;

import com.maliexplorer_backend.model.IngredientModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IngredientRepository extends JpaRepository<IngredientModel, Long> {
}
