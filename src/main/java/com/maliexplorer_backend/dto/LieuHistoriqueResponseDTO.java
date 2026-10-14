package com.maliexplorer_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LieuHistoriqueResponseDTO {

    private Long idLieu;
    private String nomLieuHisto;
    private String description;
    private String epoque;
    private String cordonnees;
    private Double latitude;
    private Double longitude;
    private String imageUrl;
    private Boolean classeUnesco;
    private String panorama360Url;
    private VilleSummaryDTO ville;
    private String region;
    private String nomVille;

    @com.fasterxml.jackson.annotation.JsonProperty("id")
    public Long getId() {
        return this.idLieu;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("nom")
    public String getNom() {
        return this.nomLieuHisto;
    }
}
