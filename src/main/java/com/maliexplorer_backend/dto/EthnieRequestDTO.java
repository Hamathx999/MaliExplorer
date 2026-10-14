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
<<<<<<< HEAD
    @com.fasterxml.jackson.annotation.JsonAlias({"nom", "nomEthnie"})
=======
    @JsonAlias({"nomEthnie", "nom"})
>>>>>>> 81c82d1b135213e0cb34fa3b6388b507d7f1b919
    private String nom;

    private String region;

    private String ville;

    @com.fasterxml.jackson.annotation.JsonAlias({"langues", "langue"})
    private String langues;

    @com.fasterxml.jackson.annotation.JsonAlias({"population", "populationEstimee"})
    private String population;

<<<<<<< HEAD
    private String description;

    @com.fasterxml.jackson.annotation.JsonAlias({"imageUrl", "photoUrl", "photoHomme"})
    private String imageUrl;

=======
    @JsonAlias({"langues", "langue"})
    private String langue;

    private String description;

    @Size(max = 5000, message = "L'URL de l'image ne peut pas dépasser 5000 caractères")
    @JsonAlias({"image", "photoUrl", "image_url"})
    private String imageUrl;

    @JsonAlias({"images", "imageUrls"})
    private List<String> images;

>>>>>>> 81c82d1b135213e0cb34fa3b6388b507d7f1b919
    private List<Long> regionIds;

    private List<Long> platIds;
}

