package com.maliexplorer_backend.repository;

import com.maliexplorer_backend.model.ReligionModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReligionRepository extends JpaRepository<ReligionModel, Long> {

    List<ReligionModel> findByPublieTrueOrderByOrdreAscIdAsc();

    List<ReligionModel> findAllByOrderByOrdreAscIdAsc();

    @Query("SELECT r FROM ReligionModel r WHERE r.publie = true AND (" +
            "LOWER(r.nom) LIKE LOWER(CONCAT('%', :kw, '%')) OR " +
            "LOWER(r.categorie) LIKE LOWER(CONCAT('%', :kw, '%')) OR " +
            "LOWER(r.description) LIKE LOWER(CONCAT('%', :kw, '%')) OR " +
            "LOWER(r.branches) LIKE LOWER(CONCAT('%', :kw, '%')) OR " +
            "LOWER(r.histoire) LIKE LOWER(CONCAT('%', :kw, '%')))")
    List<ReligionModel> searchReligions(@Param("kw") String keyword);
}

