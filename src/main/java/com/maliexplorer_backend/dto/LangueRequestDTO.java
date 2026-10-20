package com.maliexplorer_backend.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * DTO de requête pour la création et la modification d'une langue nationale.
 * Supporte des alias JSON pour assurer la compatibilité entre Angular et Flutter.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LangueRequestDTO {

    @NotBlank(message = "Le nom de la langue est obligatoire")
    @JsonAlias({"nomLangue", "nom"})
    private String nom;

    private String nomLocal;

    private String description;

    private String locuteurs;

    private String regions;

    @JsonAlias({"photoUrl", "imageUrl"})
    private String imageUrl;

    private String lexiqueJson;

    @JsonAlias({"ordre", "ordreAffichage"})
    private Integer ordreAffichage;

    @JsonAlias({"publie", "estPublie"})
    private Boolean publie;
}
