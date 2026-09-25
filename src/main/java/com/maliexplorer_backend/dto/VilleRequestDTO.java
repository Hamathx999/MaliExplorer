package com.maliexplorer_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VilleRequestDTO {

    @NotBlank(message = "Le nom de la ville est obligatoire")
    @Size(max = 100, message = "Le nom ne peut pas dépasser 100 caractères")
    private String nom;

    private String region;

    private String nbreHbts;

    private String description;

    private String cordonnees;

    private Double latitude;

    private Double longitude;

    private Boolean estCapitale;

    @Size(max = 500, message = "L'URL de l'image ne peut pas dépasser 500 caractères")
    private String imageUrl;

    private Long idUsers;

    @NotNull(message = "L'identifiant de la région est obligatoire")
    private Long idRegion;

    public Long getRegionId() { return this.idRegion; }
    public void setRegionId(Long idRegion) { this.idRegion = idRegion; }
}
