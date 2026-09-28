package com.maliexplorer_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlatRequestDTO {

    @NotBlank(message = "Le nom du plat est obligatoire")
    @Size(max = 150, message = "Le nom ne peut pas dépasser 150 caractères")
    private String nom;

    @Size(max = 150, message = "Le nom alternatif ne peut pas dépasser 150 caractères")
    private String description;

    private Integer nbrePersonnes;
@Positive(message = "Le temps de préparation doit être un nombre positif")
    @Size(max = 500, message = "L'URL de l'image ne peut pas dépasser 500 caractères")
    private List<Long> ethnieIds;

    private List<Long> regionIds;
}
