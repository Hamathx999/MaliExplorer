package com.maliexplorer_backend.service;

import com.maliexplorer_backend.dto.ReligionRequestDTO;
import com.maliexplorer_backend.dto.ReligionResponseDTO;

import java.util.List;

public interface ReligionService {
    List<ReligionResponseDTO> getPublishedReligions();
    List<ReligionResponseDTO> getAllReligions();
    ReligionResponseDTO getReligionById(Long id);
    ReligionResponseDTO createReligion(ReligionRequestDTO requestDTO);
    ReligionResponseDTO updateReligion(Long id, ReligionRequestDTO requestDTO);
    void deleteReligion(Long id);
    List<ReligionResponseDTO> searchReligions(String keyword);
}

