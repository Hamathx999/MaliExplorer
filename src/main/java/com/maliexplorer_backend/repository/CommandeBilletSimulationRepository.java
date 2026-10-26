package com.maliexplorer_backend.repository;

import com.maliexplorer_backend.model.CommandeBilletSimulationModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommandeBilletSimulationRepository extends JpaRepository<CommandeBilletSimulationModel, Long> {

    Optional<CommandeBilletSimulationModel> findByReferenceCommande(String referenceCommande);

    List<CommandeBilletSimulationModel> findByIdEvenement(Long idEvenement);

    List<CommandeBilletSimulationModel> findByIdEvenementInOrderByDateCreationDesc(List<Long> idEvenements);

    List<CommandeBilletSimulationModel> findByIdUserOrderByDateCreationDesc(Integer idUser);

    List<CommandeBilletSimulationModel> findByEmailAcheteurOrderByDateCreationDesc(String emailAcheteur);

    List<CommandeBilletSimulationModel> findByEmailPromoteurOrderByDateCreationDesc(String emailPromoteur);

    List<CommandeBilletSimulationModel> findAllByOrderByDateCreationDesc();

    @Query("SELECT COALESCE(SUM(c.quantiteBillets), 0) FROM CommandeBilletSimulationModel c WHERE c.emailPromoteur = :emailPromoteur AND c.statutSimulation != 'CANCELLED'")
    long sumBilletsVendusByEmailPromoteur(@Param("emailPromoteur") String emailPromoteur);

    @Query("SELECT COALESCE(SUM(c.quantiteBillets), 0) FROM CommandeBilletSimulationModel c WHERE c.idEvenement = :idEvenement AND c.statutSimulation != 'CANCELLED'")
    long sumBilletsVendusByEvenement(@Param("idEvenement") Long idEvenement);

    long countByEmailPromoteurAndStatutSimulationNot(String emailPromoteur, CommandeBilletSimulationModel.StatutSimulation statut);
}

