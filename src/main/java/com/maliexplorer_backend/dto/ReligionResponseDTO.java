package com.maliexplorer_backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReligionResponseDTO {

    private Long id;
    private String nom;
    private String categorie;
    private String description;
    private String histoire;
    private String pratiques;
    private String fetes;
    private String lieuxDeCulte;
    private String regionsConcernees;
    private String branches;
    private String proportionPopulation;
    private String nombrePratiquantsEstime;
    private String sourceStatistiques;
    private String anneeStatistiques;
    private String methodologieStatistiques;
    private String imageUrl;
    private Integer ordre;
    private Boolean publie;
    private LocalDateTime dateCreation;
    private LocalDateTime dateModification;

    @JsonProperty("descriptionHistorique")
    public String getDescriptionHistorique() {
        return (description != null && !description.isBlank()) ? description : histoire;
    }

    @JsonProperty("pourcentageEstime")
    public Double getPourcentageEstime() {
        if (proportionPopulation == null || proportionPopulation.isBlank()) {
            return null;
        }
        try {
            String clean = proportionPopulation.replace("%", "").replace(",", ".").trim();
            return Double.parseDouble(clean);
        } catch (Exception e) {
            return null;
        }
    }

    @JsonProperty("courantsOuBranches")
    public List<String> getCourantsOuBranches() {
        if (branches == null || branches.isBlank()) {
            return Collections.emptyList();
        }
        return Arrays.stream(branches.split("[\\n•,;]+"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    @JsonProperty("lieuxEmblematiques")
    public String getLieuxEmblematiques() {
        return lieuxDeCulte;
    }

    @JsonProperty("pratiquesEtFetes")
    public String getPratiquesEtFetes() {
        if (pratiques != null && !pratiques.isBlank() && fetes != null && !fetes.isBlank()) {
            return pratiques + " / " + fetes;
        }
        return (pratiques != null && !pratiques.isBlank()) ? pratiques : fetes;
    }

    @JsonProperty("ordreAffichage")
    public Integer getOrdreAffichage() {
        return ordre;
    }

    @JsonProperty("estPublie")
    public Boolean getEstPublie() {
        return publie;
    }

    @JsonProperty("sourceStatistique")
    public String getSourceStatistique() {
        return sourceStatistiques;
    }

    @JsonProperty("anneeStatistique")
    public Integer getAnneeStatistique() {
        if (anneeStatistiques == null || anneeStatistiques.isBlank()) {
            return null;
        }
        try {
            return Integer.parseInt(anneeStatistiques.trim());
        } catch (Exception e) {
            return null;
        }
    }
}

