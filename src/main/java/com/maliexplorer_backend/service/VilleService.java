package com.maliexplorer_backend.service;

import com.maliexplorer_backend.dto.VilleRequestDTO;
import com.maliexplorer_backend.dto.VilleResponseDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface VilleService {

    Page<VilleResponseDTO> getAllVilles(Pageable pageable);

    List<VilleResponseDTO> getAllVilles();

    VilleResponseDTO getVilleById(Long id);

    List<VilleResponseDTO> getVillesByRegion(Long regionId);

    VilleResponseDTO createVille(VilleRequestDTO requestDTO);

    VilleResponseDTO updateVille(Long id, VilleRequestDTO requestDTO);

    void deleteVille(Long id);

    List<VilleResponseDTO> searchVilles(String keyword);
}
