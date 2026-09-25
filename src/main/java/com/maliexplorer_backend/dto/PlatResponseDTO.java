package com.maliexplorer_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlatResponseDTO {

    private Long id;
    private String nom;
    private String nomAlternatif;
    private String description;
    private String ingredients;
    private Integer tempsPreparation;
    private String imageUrl;
    private Long idAdministrateur;
    private List<RegionSummaryDTO> regions;
    private List<EthnieSummaryDTO> ethnies;
}
