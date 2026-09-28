package com.maliexplorer_backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LieuHistoriqueRequestDTO {

    @NotBlank(message = "Le nom du lieu historique est obligatoire")
    private String nomLieuHisto;

    private String description;
    private String epoque;
    private String cordonnees;
    private Long villeId;
}
