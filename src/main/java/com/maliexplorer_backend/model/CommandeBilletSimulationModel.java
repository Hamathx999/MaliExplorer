package com.maliexplorer_backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "commandes_billet_simulation")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommandeBilletSimulationModel {

    public enum CanalPaiement {
        ORANGE_MONEY,
        WAVE,
        MASTERCARD
    }

    public enum StatutSimulation {
        SIMULATED,
        CONFIRMED,
        CANCELLED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reference_commande", unique = true, nullable = false, length = 64)
    private String referenceCommande;

    @NotNull
    @Column(name = "id_evenement", nullable = false)
    private Long idEvenement;

    private String titreEvenement;

    private String dateDebutEvenement;

    private String heureDebutEvenement;

    private String dateFinEvenement;

    private String heureFinEvenement;

    private String lieuEvenement;

    private String villeEvenement;

    private Integer idUser;

    private String nomAcheteur;

    private String emailAcheteur;

    private String telephoneAcheteur;

    @NotNull
    @Min(value = 1, message = "Au moins un billet est requis")
    @Column(name = "quantite_billets", nullable = false)
    private int quantiteBillets;

    @NotNull
    @Column(name = "prix_unitaire", nullable = false)
    private double prixUnitaire;

    @NotNull
    @Column(name = "montant_total", nullable = false)
    private double montantTotal;

    @Builder.Default
    private String devise = "FCFA";

    @Enumerated(EnumType.STRING)
    @Column(name = "canal_paiement", nullable = false, length = 30)
    private CanalPaiement canalPaiement;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut_simulation", nullable = false, length = 30)
    @Builder.Default
    private StatutSimulation statutSimulation = StatutSimulation.SIMULATED;

    @Column(name = "email_promoteur")
    private String emailPromoteur;

    private LocalDateTime dateCreation;

    @PrePersist
    void onCreate() {
        if (dateCreation == null) {
            dateCreation = LocalDateTime.now();
        }
        if (statutSimulation == null) {
            statutSimulation = StatutSimulation.SIMULATED;
        }
    }
}

