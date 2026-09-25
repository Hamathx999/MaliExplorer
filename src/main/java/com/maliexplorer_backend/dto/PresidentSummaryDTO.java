package com.maliexplorer_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PresidentSummaryDTO {
    private Long id;
    private String prenom;
    private String nom;
    private String periodeMandat;
    private String photoUrl;
}
