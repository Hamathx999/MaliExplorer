package com.maliexplorer_backend.dto;

import com.maliexplorer_backend.model.IngredientModel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlatRequestDTO {

    @NotBlank(message = "Le nom du plat est obligatoire")
    @Size(max = 150, message = "Le nom ne peut pas dépasser 150 caractères")
    @com.fasterxml.jackson.annotation.JsonAlias({"nomPlat", "nom"})
    private String nom;

    private String description;

    private Integer nbrePersonnes;

    @Positive(message = "Le temps de préparation doit être un nombre positif")
    private Integer tempsPreparation;

    @Size(max = 5000, message = "L'URL de l'image ne peut pas dépasser 5000 caractères")
    @com.fasterxml.jackson.annotation.JsonProperty("imageUrl")
    @com.fasterxml.jackson.annotation.JsonAlias({"image", "photoUrl", "image_url"})
    private String imageUrl;

    @com.fasterxml.jackson.annotation.JsonProperty("images")
    @com.fasterxml.jackson.annotation.JsonAlias({"images", "imageUrls"})
    private List<String> images;

    @com.fasterxml.jackson.annotation.JsonAlias({"ingredientIds", "ingredientsIds", "idIngredients"})
    private List<Long> ingredientIds;

    @com.fasterxml.jackson.annotation.JsonAlias({"ingredientPrincipal", "ingredients"})
    private String ingredientPrincipal;

    private Set<IngredientModel> ingredientModelList;

    private String region;

    private String difficulte;

    private List<Long> ethnieIds;

    private List<Long> regionIds;
}
