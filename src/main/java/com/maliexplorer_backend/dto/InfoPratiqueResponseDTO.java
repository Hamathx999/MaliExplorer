package com.maliexplorer_backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;

/**
 * DTO de réponse pour la restitution d'une fiche d'information pratique.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InfoPratiqueResponseDTO {

    private Long id;
    private String titre;
    private String categorie;
    private String description;
    private String conseils;
    private String coordonnees;
    private String badge;
    private String badgeColor;
    private String icon;
    private String imageUrl;
    private Integer ordreAffichage;
    private Boolean publie;
    private LocalDateTime dateCreation;
    private LocalDateTime dateModification;

    @JsonProperty("ordre")
    public Integer getOrdre() {
        return ordreAffichage;
    }

    @JsonProperty("estPublie")
    public Boolean getEstPublie() {
        return publie;
    }

    @JsonProperty("photoUrl")
    public String getPhotoUrl() {
        return imageUrl;
    }
}
