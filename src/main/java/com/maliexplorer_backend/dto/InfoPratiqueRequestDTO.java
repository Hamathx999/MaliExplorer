package com.maliexplorer_backend.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * DTO de requête pour la création et la mise à jour d'une fiche d'information pratique.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InfoPratiqueRequestDTO {

    @NotBlank(message = "Le titre est obligatoire")
    private String titre;

    @NotBlank(message = "La catégorie est obligatoire")
    private String categorie;

    private String description;

    private String conseils;

    private String coordonnees;

    private String badge;

    private String badgeColor;

    private String icon;

    @JsonAlias({"photoUrl", "imageUrl"})
    private String imageUrl;

    @JsonAlias({"ordre", "ordreAffichage"})
    private Integer ordreAffichage;

    @JsonAlias({"publie", "estPublie"})
    private Boolean publie;
}
