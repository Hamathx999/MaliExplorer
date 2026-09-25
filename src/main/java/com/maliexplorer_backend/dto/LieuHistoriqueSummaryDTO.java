package com.maliexplorer_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LieuHistoriqueSummaryDTO {

    private Long idLieu;
    private String nomHistoire;
    private String epoque;
    private String imageUrl;
    private Double latitude;
    private Double longitude;
}
