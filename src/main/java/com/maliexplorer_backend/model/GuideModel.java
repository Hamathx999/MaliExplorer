package com.maliexplorer_backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "guides")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class GuideModel extends utilisateurModel {

    private Long idGuide;

    @Min(value = 0, message = "L'expérience ne peut pas être négative !")
    private int experience;

    @Column(columnDefinition = "TEXT")
    private String description;

    @NotBlank(message = "La langue parlée est obligatoire !")
    private String langue;

    private String pieceIdentite;

    private Integer idAdministrateur;

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
