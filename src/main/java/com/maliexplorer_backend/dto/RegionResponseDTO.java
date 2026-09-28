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
public class RegionResponseDTO {

    private Long id;
    private String nom;
    private String description;
    private Long nbreHbt;
    private List<VilleSummaryDTO> villes;
    private List<EthnieSummaryDTO> ethnies;
    private List<PlatSummaryDTO> plats;
}
