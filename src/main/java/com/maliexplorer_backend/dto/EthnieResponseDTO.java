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
    private String ville;
    private String langues;
    private String population;
    private String description;
    private String imageUrl;
    private List<RegionSummaryDTO> regions;
    private List<PlatSummaryDTO> plats;

    @com.fasterxml.jackson.annotation.JsonProperty("idEthnie")
    public Long getIdEthnie() {
        return this.id;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("nomEthnie")
    public String getNomEthnie() {
        return this.nom;
    }
}
