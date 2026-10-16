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

    @NotBlank(message = "Le nom de l'organisation est obligatoire !")
    private String nomOrganisation;

    private String pieceIdentite;

    @Column(name = "recherche_partenariat", nullable = false)
    private boolean recherchePartenariat = false;

    @Column(name = "titre_projet", length = 150)
    private String titreProjet;

    @Column(name = "besoin_partenariat", columnDefinition = "TEXT")
    private String besoinPartenariat;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut_moderation", length = 30)
    private StatutModeration statutModeration = StatutModeration.VALIDE;

    @Column(name = "motif_rejet", length = 500)
    private String motifRejet;
}
