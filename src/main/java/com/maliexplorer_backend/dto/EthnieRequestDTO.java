package com.maliexplorer_backend.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
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
    @JsonAlias({"nom", "nomEthnie"})
    private String nom;

    private String region;

    private String ville;

    @com.fasterxml.jackson.annotation.JsonAlias({"langues", "langue"})
    private String langues;

    @com.fasterxml.jackson.annotation.JsonAlias({"population", "populationEstimee"})
    private String population;

    @JsonAlias({"langues", "langue"})
    private String langue;

    private String description;

    @Size(max = 5000, message = "L'URL de l'image ne peut pas dépasser 5000 caractères")
    @JsonAlias({"imageUrl", "image", "photoUrl", "photoHomme", "image_url"})
    private String imageUrl;

    @JsonAlias({"images", "imageUrls"})
    private List<String> images;
    private List<Long> regionIds;

    private List<Long> platIds;
}

