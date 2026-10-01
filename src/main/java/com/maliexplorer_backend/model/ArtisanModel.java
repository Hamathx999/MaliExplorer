package com.maliexplorer_backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "artisans")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class ArtisanModel extends utilisateurModel {

    @NotBlank(message = "Le type d'artisanat est obligatoire !")
    private String typeArtisanat;

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
