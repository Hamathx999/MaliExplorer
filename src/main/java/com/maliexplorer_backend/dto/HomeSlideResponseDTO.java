package com.maliexplorer_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HomeSlideResponseDTO {

    private Long id;
    private String titre;
    private String sousTitre;
    private String legende;
    private String imageUrl;
    private String lienAction;
    private Integer ordre;
    private Boolean actif;
    private Boolean prioritaire;
    private LocalDateTime dateCreation;
    private LocalDateTime dateModification;

    @com.fasterxml.jackson.annotation.JsonProperty("estActif")
    public Boolean getEstActif() {
        return this.actif;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("estPrincipal")
    public Boolean getEstPrincipal() {
        return this.prioritaire;
    }
}

