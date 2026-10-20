package com.maliexplorer_backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;

/**
 * DTO de réponse pour la restitution d'une langue nationale.
 * Offre des alias JSON calculés pour assurer une compatibilité bidirectionnelle parfaite.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LangueResponseDTO {

    private Long id;
    private String nom;
    private String nomLocal;
    private String description;
    private String locuteurs;
    private String regions;
    private String imageUrl;
    private String lexiqueJson;
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

    @JsonProperty("nomLangue")
    public String getNomLangue() {
        return nom;
    }
}
