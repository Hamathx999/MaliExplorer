package com.maliexplorer_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EthnieResponseDTO {

    private Long id;
    private String nom;
    private String langue;
    private String description;
    private String imageUrl;
    private Set<RegionSummaryDTO> regions;
    private Set<PlatSummaryDTO> plats;
}
