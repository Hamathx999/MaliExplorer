package com.maliexplorer_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EthnieRequestDTO {

    @NotBlank(message = "Le nom de l'ethnie est obligatoire")
    @Size(max = 100, message = "Le nom ne peut pas dépasser 100 caractères")
    @com.fasterxml.jackson.annotation.JsonAlias({"nom", "nomEthnie"})
    private String nom;

    private String region;

    private String ville;

    @com.fasterxml.jackson.annotation.JsonAlias({"langues", "langue"})
    private String langues;

    @com.fasterxml.jackson.annotation.JsonAlias({"population", "populationEstimee"})
    private String population;

    private String description;

    @com.fasterxml.jackson.annotation.JsonAlias({"imageUrl", "photoUrl", "photoHomme"})
    private String imageUrl;

    private List<Long> regionIds;

    private List<Long> platIds;
}

