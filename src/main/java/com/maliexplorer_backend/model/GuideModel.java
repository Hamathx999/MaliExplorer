package com.maliexplorer_backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "guides")
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class GuideModel extends utilisateurModel {

    public GuideModel() {
        super();
    }

    private Long idGuide;

    @Min(value = 0, message = "L'expérience ne peut pas être négative !")
    private int experience;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String langue = "Français, Bambara";

    @Column(name = "piece_identite", columnDefinition = "TEXT")
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
    private StatutModeration statutModeration = StatutModeration.EN_ATTENTE_VALIDATION;

    @Column(name = "motif_rejet", length = 500)
    private String motifRejet;

    @PrePersist
    @PreUpdate
    void ensureDefaults() {
        if (this.langue == null || this.langue.isBlank()) {
            this.langue = "Français, Bambara";
        }
    }
}
