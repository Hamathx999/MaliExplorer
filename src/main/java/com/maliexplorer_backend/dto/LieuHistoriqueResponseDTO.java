package com.maliexplorer_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LieuHistoriqueResponseDTO {

    private Long idLieu;
    private String nomHistoire;
    private String description;
    private String epoque;
    private String cordonnees;
    private Double latitude;
    private Double longitude;
    private String imageUrl;
    private String panorama360Url;
    private Long idUsers;
    private VilleSummaryDTO ville;
}
