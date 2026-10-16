package com.maliexplorer_backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO regroupant les séries temporelles d'activité réelle sur 7 jours, 30 jours et 12 mois.
 * Les clés JSON sont annotées pour correspondre strictement aux attentes du client web.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeriesTemporellesDTO {

    /** Série chronologique d'activité sur les 7 derniers jours */
    @JsonProperty("7j")
    private List<PointTemporelDTO> septJours;

    /** Série chronologique d'activité sur les 30 derniers jours */
    @JsonProperty("30j")
    private List<PointTemporelDTO> trenteJours;

    /** Série chronologique d'activité sur les 12 derniers mois */
    @JsonProperty("12m")
    private List<PointTemporelDTO> douzeMois;
}
