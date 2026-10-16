package com.maliexplorer_backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HomeSlideRequestDTO {

    @NotBlank(message = "Le titre est obligatoire")
    private String titre;

    private String sousTitre;
    private String legende;

    @NotBlank(message = "L'URL de l'image est obligatoire")
    private String imageUrl;

    private String lienAction;
    private Integer ordre;
    @com.fasterxml.jackson.annotation.JsonAlias({"actif", "estActif"})
    private Boolean actif;

    @com.fasterxml.jackson.annotation.JsonAlias({"prioritaire", "estPrincipal"})
    private Boolean prioritaire;
}

