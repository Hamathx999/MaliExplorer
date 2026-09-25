package com.maliexplorer_backend.repository;

import com.maliexplorer_backend.model.LieuHistorique;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LieuHistoriqueRepository extends JpaRepository<LieuHistorique, Long> {

    List<LieuHistorique> findByVilleIdVille(Long idVille);

    List<LieuHistorique> findByNomHistoireContainingIgnoreCase(String keyword);
}
