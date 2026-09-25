package com.maliexplorer_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MarqueurCarteDTO {

    private String id;
    private String nom;
    private String type; // "LIEU_HISTORIQUE", "VILLE", "POINT_INTERET"
    private Double latitude;
    private Double longitude;
    private String description;
    private String imageUrl;
    private String panorama360Url;
    private Long referenceId;
    private String categorie;
}
