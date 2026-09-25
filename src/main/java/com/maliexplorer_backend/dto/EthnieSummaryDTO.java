package com.maliexplorer_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EthnieSummaryDTO {
    private Long id;
    private String nom;
    private String region;
    private String langue;
    private String imageUrl;
}
