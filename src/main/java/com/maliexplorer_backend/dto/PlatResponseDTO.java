package com.maliexplorer_backend.dto;

import com.maliexplorer_backend.model.IngredientModel;
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
public class PlatResponseDTO {

    private Long id;
    private String nom;
    private String description;
    private Integer nbrePersonnes;
    private Integer tempsPreparation;
    private String imageUrl;
    private List<String> images;
    private Set<IngredientModel> ingredientModelList;
    private List<RegionSummaryDTO> regions;
    private List<EthnieSummaryDTO> ethnies;

    @com.fasterxml.jackson.annotation.JsonProperty("ingredients")
    public List<String> getIngredients() {
        if (ingredientModelList == null) return java.util.List.of();
        return ingredientModelList.stream()
                .map(i -> i.getNom() != null ? i.getNom() : i.getNomPlat())
                .filter(java.util.Objects::nonNull)
                .toList();
    }

    @com.fasterxml.jackson.annotation.JsonProperty("ingredientIds")
    public List<Long> getIngredientIds() {
        if (ingredientModelList == null) return java.util.List.of();
        return ingredientModelList.stream()
                .map(IngredientModel::getIdIngredient)
                .filter(java.util.Objects::nonNull)
                .toList();
    }
}
