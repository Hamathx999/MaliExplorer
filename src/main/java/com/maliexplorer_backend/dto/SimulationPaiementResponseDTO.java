package com.maliexplorer_backend.dto;

import com.maliexplorer_backend.model.CommandeBilletSimulationModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SimulationPaiementResponseDTO {
    private Long id;
    private String referenceCommande;
    private Long idEvenement;
    private String titreEvenement;
    private String dateDebutEvenement;
    private String heureDebutEvenement;
    private String dateFinEvenement;
    private String heureFinEvenement;
    private String lieuEvenement;
    private String villeEvenement;
    private String nomAcheteur;
    private int quantiteBillets;
    private double prixUnitaire;
    private double montantTotal;
    private String devise;
    private CommandeBilletSimulationModel.CanalPaiement canalPaiement;
    private CommandeBilletSimulationModel.StatutSimulation statutSimulation;
    private boolean valide;
    private String message;
    private LocalDateTime dateCreation;
}

