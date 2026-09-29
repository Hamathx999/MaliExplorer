package com.maliexplorer_backend.service;

import com.maliexplorer_backend.dto.LieuHistoriqueRequestDTO;
import com.maliexplorer_backend.dto.LieuHistoriqueResponseDTO;

import java.util.List;

public interface LieuHistoriqueService {

    List<LieuHistoriqueResponseDTO> getAllLieux();

    LieuHistoriqueResponseDTO getLieuById(Long id);

    List<LieuHistoriqueResponseDTO> getLieuxByVille(Long villeId);

    LieuHistoriqueResponseDTO createLieu(LieuHistoriqueRequestDTO requestDTO);

    LieuHistoriqueResponseDTO updateLieu(Long id, LieuHistoriqueRequestDTO requestDTO);

    void deleteLieu(Long id);

    List<LieuHistoriqueResponseDTO> searchLieux(String keyword);

    List<LieuHistoriqueResponseDTO> getLieuxWithPanorama360();
}
