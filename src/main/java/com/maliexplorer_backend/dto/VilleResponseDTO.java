package com.maliexplorer_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VilleResponseDTO {

    private Long id;
    private String nom;
    private String region;
    private String nbreHbt;
    private String description;
    private String cordonnees;
    private String imageUrl;
<<<<<<< HEAD
    private String superficie;
    private Boolean misEnAvantHome;
    private Integer ordreHome;
=======
    private List<String> images;
>>>>>>> 81c82d1b135213e0cb34fa3b6388b507d7f1b919
    private RegionSummaryDTO regionParent;
    private List<LieuHistoriqueSummaryDTO> lieuxHistoriques;
}
