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
public class EthnieResponseDTO {

    private Long id;
    private String nom;
    private String region;
    private String population;
    private String langue;
    private String description;
    private String imageUrl;
    private Long idUsers;
    private List<RegionSummaryDTO> regions;
    private List<PlatSummaryDTO> plats;
}
