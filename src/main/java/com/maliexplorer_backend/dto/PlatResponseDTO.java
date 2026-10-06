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
    private Set<IngredientModel> ingredientModelList;
    private List<RegionSummaryDTO> regions;
    private List<EthnieSummaryDTO> ethnies;
}
