package com.maliexplorer_backend.service;

import com.maliexplorer_backend.dto.PointInteretRequestDTO;
import com.maliexplorer_backend.dto.PointInteretResponseDTO;

import java.util.List;

public interface PointInteretService {

    List<PointInteretResponseDTO> getAllPoints();

    PointInteretResponseDTO getPointById(Long id);

    List<PointInteretResponseDTO> getPointsByRegion(Long regionId);

    List<PointInteretResponseDTO> getPointsByType(String type);

    PointInteretResponseDTO createPoint(PointInteretRequestDTO requestDTO);

    PointInteretResponseDTO updatePoint(Long id, PointInteretRequestDTO requestDTO);

    void deletePoint(Long id);
}
