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
public class ReligionRequestDTO {

    @NotBlank(message = "Le nom de la religion ou tradition est obligatoire")
    private String nom;

    private String categorie;
    private String description;
    private String histoire;
    private String pratiques;
    private String fetes;
    private String lieuxDeCulte;
    private String regionsConcernees;
    private String branches;
    private String proportionPopulation;
    private String nombrePratiquantsEstime;
    private String sourceStatistiques;
    private String anneeStatistiques;
    private String methodologieStatistiques;
    private String imageUrl;
    private Integer ordre;
    private Boolean publie;
}

