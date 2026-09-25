package com.maliexplorer_backend.service;

import com.maliexplorer_backend.dto.RegionRequestDTO;
import com.maliexplorer_backend.dto.RegionResponseDTO;

import java.util.List;

public interface RegionService {

    List<RegionResponseDTO> getAllRegions();

    RegionResponseDTO getRegionById(Long id);

    RegionResponseDTO createRegion(RegionRequestDTO requestDTO);

    RegionResponseDTO updateRegion(Long id, RegionRequestDTO requestDTO);

    void deleteRegion(Long id);

    List<RegionResponseDTO> searchRegions(String keyword);
}
