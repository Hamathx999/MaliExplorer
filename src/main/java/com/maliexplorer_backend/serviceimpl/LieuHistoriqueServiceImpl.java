package com.maliexplorer_backend.serviceimpl;

import com.maliexplorer_backend.dto.LieuHistoriqueRequestDTO;
import com.maliexplorer_backend.dto.LieuHistoriqueResponseDTO;
import com.maliexplorer_backend.dto.VilleSummaryDTO;
import com.maliexplorer_backend.exception.ResourceNotFoundException;
import com.maliexplorer_backend.model.LieuHistoriqueModel;
import com.maliexplorer_backend.model.VilleModel;
import com.maliexplorer_backend.repository.LieuHistoriqueRepository;
import com.maliexplorer_backend.repository.VilleRepository;
import com.maliexplorer_backend.service.LieuHistoriqueService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class LieuHistoriqueServiceImpl implements LieuHistoriqueService {

    private final LieuHistoriqueRepository lieuHistoriqueRepository;
    private final VilleRepository villeRepository;

    @Override
    @Transactional(readOnly = true)
    public List<LieuHistoriqueResponseDTO> getAllLieux() {
        return lieuHistoriqueRepository.findAll()
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public LieuHistoriqueResponseDTO getLieuById(Long id) {
        LieuHistoriqueModel lieu = findLieuOrThrow(id);
        return mapToResponseDTO(lieu);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LieuHistoriqueResponseDTO> getLieuxByVille(Long villeId) {
        return lieuHistoriqueRepository.findByVilleIdVille(villeId)
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public LieuHistoriqueResponseDTO createLieu(LieuHistoriqueRequestDTO requestDTO) {
        VilleModel ville = null;
        if (requestDTO.getVilleId() != null) {
            ville = villeRepository.findById(requestDTO.getVilleId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "VilleModel introuvable avec l'ID : " + requestDTO.getVilleId()));
        }

        LieuHistoriqueModel lieu = LieuHistoriqueModel.builder()
                .nomLieu(requestDTO.getNomLieuHisto())
                .description(requestDTO.getDescription())
                .epoque(requestDTO.getEpoque())
                .cordonnees(requestDTO.getCordonnees())
                .ville(ville)
                .build();

        LieuHistoriqueModel saved = lieuHistoriqueRepository.save(lieu);
        return mapToResponseDTO(saved);
    }

    @Override
    public LieuHistoriqueResponseDTO updateLieu(Long id, LieuHistoriqueRequestDTO requestDTO) {
        LieuHistoriqueModel lieu = findLieuOrThrow(id);

        if (requestDTO.getVilleId() != null) {
            VilleModel ville = villeRepository.findById(requestDTO.getVilleId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "VilleModel introuvable avec l'ID : " + requestDTO.getVilleId()));
            lieu.setVille(ville);
        } else {
            lieu.setVille(null);
        }

        lieu.setNomLieu(requestDTO.getNomLieuHisto());
        lieu.setDescription(requestDTO.getDescription());
        lieu.setEpoque(requestDTO.getEpoque());
        lieu.setCordonnees(requestDTO.getCordonnees());

        LieuHistoriqueModel updated = lieuHistoriqueRepository.save(lieu);
        return mapToResponseDTO(updated);
    }

    @Override
    public void deleteLieu(Long id) {
        LieuHistoriqueModel lieu = findLieuOrThrow(id);
        lieuHistoriqueRepository.delete(lieu);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LieuHistoriqueResponseDTO> searchLieux(String keyword) {
        return lieuHistoriqueRepository.findByNomLieuContainingIgnoreCase(keyword)
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LieuHistoriqueResponseDTO> getLieuxWithPanorama360() {
        return lieuHistoriqueRepository.findAll()
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    private LieuHistoriqueModel findLieuOrThrow(Long id) {
        return lieuHistoriqueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lieu historique introuvable avec l'ID : " + id));
    }

    private LieuHistoriqueResponseDTO mapToResponseDTO(LieuHistoriqueModel lieu) {
        VilleSummaryDTO villeSummary = null;
        if (lieu.getVille() != null) {
            villeSummary = VilleSummaryDTO.builder()
                    .id(lieu.getVille().getIdVille())
                    .nom(lieu.getVille().getNomVille())
                    .build();
        }

        return LieuHistoriqueResponseDTO.builder()
                .idLieu(lieu.getIdLieu())
                .nomLieuHisto(lieu.getNomLieu())
                .description(lieu.getDescription())
                .epoque(lieu.getEpoque())
                .cordonnees(lieu.getCordonnees())
                .ville(villeSummary)
                .build();
    }
}
