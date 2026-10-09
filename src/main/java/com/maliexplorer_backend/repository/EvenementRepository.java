package com.maliexplorer_backend.repository;

import com.maliexplorer_backend.model.EvenementModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EvenementRepository extends JpaRepository<EvenementModel, Long> {

    List<EvenementModel> findAllByOrderByDateSoumissionDesc();

    List<EvenementModel> findByStatutOrderByDateSoumissionDesc(EvenementModel.Statut statut);

    List<EvenementModel> findByEmailOrganisateur(String emailOrganisateur);
}

