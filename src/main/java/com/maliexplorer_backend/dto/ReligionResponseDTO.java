package com.maliexplorer_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReligionResponseDTO {

    private Long id;
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
    private LocalDateTime dateCreation;
    private LocalDateTime dateModification;
}

