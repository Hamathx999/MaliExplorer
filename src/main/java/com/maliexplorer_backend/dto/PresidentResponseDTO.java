package com.maliexplorer_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PresidentResponseDTO {

    private Long id;
    private String prenom;
    private String nom;
    private LocalDate dateNaissance;
    private LocalDate dateDeces;
    private String periodeMandat;
    private String biographie;
    private String photoUrl;

    @com.fasterxml.jackson.annotation.JsonProperty("idPresident")
    public Long getIdPresident() {
        return this.id;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("imageUrl")
    public String getImageUrl() {
        return this.photoUrl;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("periode")
    public String getPeriode() {
        return this.periodeMandat;
    }
}
