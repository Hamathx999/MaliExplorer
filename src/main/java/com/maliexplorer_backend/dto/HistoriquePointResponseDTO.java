package com.maliexplorer_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistoriquePointResponseDTO {

    private Long idHistorique;
    private String action;
    private int pointsGagnes;
    private String description;
    private String referenceActivite;
    private LocalDateTime dateGain;
}
