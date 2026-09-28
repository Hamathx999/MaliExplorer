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
public class VilleResponseDTO {

    private Long id;
    private String nom;
    private String region;
    private String nbreHbt;
    private String description;
    private String cordonnees;
    private RegionSummaryDTO regionParent;
    private List<LieuHistoriqueSummaryDTO> lieuxHistoriques;
}
