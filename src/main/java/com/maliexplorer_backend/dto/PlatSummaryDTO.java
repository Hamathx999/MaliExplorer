package com.maliexplorer_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlatSummaryDTO {
    private Long id;
    private String nom;
    private String nomAlternatif;
    private Integer tempsPreparation;
    private String imageUrl;
}
