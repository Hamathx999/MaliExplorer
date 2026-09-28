package com.maliexplorer_backend.service;

import com.maliexplorer_backend.dto.PlatRequestDTO;
import com.maliexplorer_backend.dto.PlatResponseDTO;

import java.util.List;

public interface PlatService {

    List<PlatResponseDTO> getAllPlats();

    PlatResponseDTO getPlatById(Long id);

    PlatResponseDTO createPlat(PlatRequestDTO requestDTO);

    PlatResponseDTO updatePlat(Long id, PlatRequestDTO requestDTO);

    void deletePlat(Long id);

    List<PlatResponseDTO> searchPlats(String keyword);

    List<PlatResponseDTO> getPlatsByRegion(Long regionId);

    List<PlatResponseDTO> getPlatsByEthnie(Long ethnieId);
}
