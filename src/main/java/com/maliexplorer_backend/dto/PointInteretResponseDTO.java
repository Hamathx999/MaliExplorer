package com.maliexplorer_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PointInteretResponseDTO {

    private Long id;
    private String nom;
    private String type;
    private Double latitude;
    private Double longitude;
    private String description;
    private String imageUrl;
    private String panorama360Url;
    private Long regionId;
}
