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
    private String langue;
    private String description;
    private String imageUrl;
<<<<<<< HEAD
=======
    private List<String> images;
>>>>>>> 81c82d1b135213e0cb34fa3b6388b507d7f1b919
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
