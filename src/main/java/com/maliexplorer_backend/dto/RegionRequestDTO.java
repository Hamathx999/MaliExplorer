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
public class RegionRequestDTO {

    @NotBlank(message = "Le nom de la région est obligatoire")
    @Size(max = 100, message = "Le nom ne peut pas dépasser 100 caractères")
    private String nom;

    @Size(max = 20, message = "Le code ne peut pas dépasser 20 caractères")
    private String code;

    private String description;

    @Positive(message = "La superficie doit être positive")
    private Double superficie;

    @Positive(message = "La population doit être positive")
    private Long population;

    @Size(max = 100, message = "Le chef-lieu ne peut pas dépasser 100 caractères")
    private String chefLieu;

    @Size(max = 500, message = "L'URL de l'image ne peut pas dépasser 500 caractères")
    private String imageUrl;

    private Long idUsers;

    private List<Long> ethnieIds;

    private List<Long> platIds;
}
