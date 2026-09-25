package com.maliexplorer_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegionSummaryDTO {
    private Long id;
    private String nom;
    private String code;
    private String chefLieu;
    private String imageUrl;
}
