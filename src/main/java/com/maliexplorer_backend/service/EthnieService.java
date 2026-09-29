package com.maliexplorer_backend.service;

import com.maliexplorer_backend.dto.EthnieRequestDTO;
import com.maliexplorer_backend.dto.EthnieResponseDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EthnieService {

    Page<EthnieResponseDTO> getAllEthnies(Pageable pageable);

    List<EthnieResponseDTO> getAllEthnies();

    EthnieResponseDTO getEthnieById(Long id);

    EthnieResponseDTO createEthnie(EthnieRequestDTO requestDTO);

    EthnieResponseDTO updateEthnie(Long id, EthnieRequestDTO requestDTO);

    void deleteEthnie(Long id);

    List<EthnieResponseDTO> searchEthnies(String keyword);

    List<EthnieResponseDTO> getEthniesByRegion(Long regionId);
}
