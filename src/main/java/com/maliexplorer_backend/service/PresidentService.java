package com.maliexplorer_backend.service;

import com.maliexplorer_backend.dto.PresidentRequestDTO;
import com.maliexplorer_backend.dto.PresidentResponseDTO;

import java.util.List;

public interface PresidentService {

    List<PresidentResponseDTO> getAllPresidents();

    PresidentResponseDTO getPresidentById(Long id);

    PresidentResponseDTO createPresident(PresidentRequestDTO requestDTO);

    PresidentResponseDTO updatePresident(Long id, PresidentRequestDTO requestDTO);

    void deletePresident(Long id);

    List<PresidentResponseDTO> searchPresidents(String keyword);
}
