package com.maliexplorer_backend.dto;

import jakarta.validation.constraints.NotBlank;
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
public class EthnieRequestDTO {

    @NotBlank(message = "Le nom de l'ethnie est obligatoire")
    @Size(max = 100, message = "Le nom ne peut pas dépasser 100 caractères")
    private String nom;

    private String region;

    private String population;

    @Size(max = 100, message = "La langue ne peut pas dépasser 100 caractères")
    private String langue;

    private String description;

    @Size(max = 500, message = "L'URL de l'image ne peut pas dépasser 500 caractères")
    private String imageUrl;

    private Long idUsers;

    private List<Long> regionIds;

    private List<Long> platIds;
}

