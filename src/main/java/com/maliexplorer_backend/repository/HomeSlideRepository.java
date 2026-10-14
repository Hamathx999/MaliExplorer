package com.maliexplorer_backend.repository;

import com.maliexplorer_backend.model.HomeSlideModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HomeSlideRepository extends JpaRepository<HomeSlideModel, Long> {
    List<HomeSlideModel> findByActifTrueOrderByPrioritaireDescOrdreAscIdAsc();
    List<HomeSlideModel> findAllByOrderByOrdreAscIdAsc();
}

