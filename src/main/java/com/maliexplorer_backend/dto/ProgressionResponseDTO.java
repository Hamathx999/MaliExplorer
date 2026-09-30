package com.maliexplorer_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProgressionResponseDTO {

    private Integer idUsers;
    private String nomComplet;
    private String email;

    private int points;
    private String badge;
    private String badgeCode;
    private String badgeDescription;

    private String nextBadge;
    private Integer pointsToNextBadge;
    private Integer pointsNextBadgeSeuil;
    private double progression;

    private String message;
}
