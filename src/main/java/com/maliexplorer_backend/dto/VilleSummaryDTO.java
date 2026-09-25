package com.maliexplorer_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VilleSummaryDTO {
    private Long id;
    private String nom;
    private Double latitude;
    private Double longitude;
    private String cordonnees;
    private Boolean estCapitale;
    private String imageUrl;
}
