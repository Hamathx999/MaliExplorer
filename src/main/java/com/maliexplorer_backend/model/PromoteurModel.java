package com.maliexplorer_backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "promoteurs")
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class PromoteurModel extends utilisateurModel {

    public PromoteurModel() {
        super();
    }

    private Long idPromoteur;

    @Column(name = "nom_organisation")
    private String nomOrganisation = "Organisation Partenaire";

    @Column(name = "piece_identite", columnDefinition = "TEXT")
    private String pieceIdentite;

    @Column(name = "recherche_partenariat", nullable = false)
    private boolean recherchePartenariat = false;

    @Column(name = "titre_projet", length = 150)
    private String titreProjet;

    @Column(name = "besoin_partenariat", columnDefinition = "TEXT")
    private String besoinPartenariat;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut_moderation", length = 30)
    private StatutModeration statutModeration = StatutModeration.EN_ATTENTE_VALIDATION;

    @Column(name = "motif_rejet", length = 500)
    private String motifRejet;

    @PrePersist
    @PreUpdate
    void ensurePromoteurDefaults() {
        if (this.nomOrganisation == null || this.nomOrganisation.isBlank()) {
            this.nomOrganisation = "Organisation " + (this.getNom() != null && !this.getNom().isBlank() ? this.getNom() : "Partenaire");
        }
    }
}
