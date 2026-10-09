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
    @Size(max = 100, message = "Le nom ne peut pas dépasser 100 caractères")
    @JsonAlias({"nomEthnie", "nom"})
    private String nom;

    private String region;

    private String population;

    @JsonAlias({"langues", "langue"})
    private String langue;

    private String description;

    @Size(max = 5000, message = "L'URL de l'image ne peut pas dépasser 5000 caractères")
    @JsonAlias({"image", "photoUrl", "image_url"})
    private String imageUrl;

    @JsonAlias({"images", "imageUrls"})
    private List<String> images;

    private List<Long> regionIds;

    private List<Long> platIds;
}

