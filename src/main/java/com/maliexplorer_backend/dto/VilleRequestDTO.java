package com.maliexplorer_backend.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;
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

    @JsonAlias("population")
    private String nbreHbt;

    private String description;

    private String cordonnees;

    @Size(max = 5000, message = "L'URL de l'image ne peut pas dépasser 5000 caractères")
    @JsonAlias({"image", "photoUrl", "image_url"})
    private String imageUrl;

    @JsonAlias({"images", "imageUrls"})
    private java.util.List<String> images;

    @JsonAlias({"regionId", "id_region"})
    private Long idRegion;

    public String getPopulation() {
        return this.nbreHbt;
    }

    public void setPopulation(String population) {
        this.nbreHbt = population;
    }

    public Long getRegionId() {
        return this.idRegion;
    }

    public void setRegionId(Long idRegion) {
        this.idRegion = idRegion;
    }
}
