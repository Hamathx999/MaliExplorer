package com.maliexplorer_backend.repository;

import com.maliexplorer_backend.model.LieuHistoriqueModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LieuHistoriqueRepository extends JpaRepository<LieuHistoriqueModel, Long> {

    List<LieuHistoriqueModel> findByVilleIdVille(Long idVille);

    List<LieuHistoriqueModel> findByNomLieuContainingIgnoreCase(String keyword);
}
