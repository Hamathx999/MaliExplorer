package com.maliexplorer_backend.repository;

import com.maliexplorer_backend.model.HistoriquePointModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoriquePointRepository extends JpaRepository<HistoriquePointModel, Long> {

    List<HistoriquePointModel> findByUtilisateurIdUsersOrderByDateGainDesc(int idUsers);

    boolean existsByUtilisateurIdUsersAndReferenceActivite(int idUsers, String referenceActivite);
}
