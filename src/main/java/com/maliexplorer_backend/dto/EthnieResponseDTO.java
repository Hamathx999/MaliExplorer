package com.maliexplorer_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import com.maliexplorer_backend.dto.RegionSummaryDTO.RegionSummaryDTOBuilder;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EthnieResponseDTO {

    private Long id;
    private String nom;
    private String region;
    private String population;
    private String langue;
    private String description;
    private String imageUrl;
    private Long idUsers;
    private List<RegionSummaryDTO> regions;
    private List<PlatSummaryDTO> plats;
	public static RegionSummaryDTOBuilder builder() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'builder'");
	}
}