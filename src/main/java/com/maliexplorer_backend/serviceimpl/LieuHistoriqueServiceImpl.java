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
            ville = villeRepository.findById(requestDTO.getVilleId()).orElse(null);
        }
        String cityName = requestDTO.getNomVille() != null && !requestDTO.getNomVille().isBlank()
                ? requestDTO.getNomVille().trim()
                : (requestDTO.getVille() != null ? requestDTO.getVille().trim() : null);

        if (ville == null && cityName != null && !cityName.isBlank()) {
            ville = villeRepository.findByNomVilleIgnoreCase(cityName).orElse(null);
        }

        String regionName = requestDTO.getRegion();
        if ((regionName == null || regionName.isBlank()) && ville != null) {
            regionName = ville.getRegion();
        }

        LieuHistoriqueModel lieu = LieuHistoriqueModel.builder()
                .nomLieu(requestDTO.getNomLieuHisto())
                .nomHistoire(requestDTO.getNomLieuHisto())
                .description(requestDTO.getDescription())
                .epoque(requestDTO.getEpoque())
                .cordonnees(requestDTO.getCordonnees())
                .panorama360Url(requestDTO.getPanorama360Url())
                .latitude(requestDTO.getLatitude())
                .longitude(requestDTO.getLongitude())
                .nomVille(cityName != null ? cityName : (ville != null ? ville.getNomVille() : null))
                .region(regionName)
                .ville(ville)
                .build();

        LieuHistoriqueModel saved = lieuHistoriqueRepository.save(lieu);
        return mapToResponseDTO(saved);
    }

    @Override
    public LieuHistoriqueResponseDTO updateLieu(Long id, LieuHistoriqueRequestDTO requestDTO) {
        LieuHistoriqueModel lieu = findLieuOrThrow(id);

        if (requestDTO.getVilleId() != null) {
            VilleModel ville = villeRepository.findById(requestDTO.getVilleId()).orElse(null);
            lieu.setVille(ville);
        }

        String cityName = requestDTO.getNomVille() != null && !requestDTO.getNomVille().isBlank()
                ? requestDTO.getNomVille().trim()
                : (requestDTO.getVille() != null ? requestDTO.getVille().trim() : null);

        if (cityName != null && !cityName.isBlank()) {
            lieu.setNomVille(cityName);
            if (lieu.getVille() == null) {
                villeRepository.findByNomVilleIgnoreCase(cityName).ifPresent(lieu::setVille);
            }
        }

        if (requestDTO.getRegion() != null && !requestDTO.getRegion().isBlank()) {
            lieu.setRegion(requestDTO.getRegion().trim());
        }

        lieu.setNomLieu(requestDTO.getNomLieuHisto());
        lieu.setNomHistoire(requestDTO.getNomLieuHisto());
        lieu.setDescription(requestDTO.getDescription());
        lieu.setEpoque(requestDTO.getEpoque());
        lieu.setCordonnees(requestDTO.getCordonnees());
        lieu.setPanorama360Url(requestDTO.getPanorama360Url());
        lieu.setLatitude(requestDTO.getLatitude());
        lieu.setLongitude(requestDTO.getLongitude());

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
                .filter(l -> l.getPanorama360Url() != null && !l.getPanorama360Url().isBlank())
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    private LieuHistoriqueModel findLieuOrThrow(Long id) {
        return lieuHistoriqueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lieu historique introuvable avec l'ID : " + id));
    }

    private LieuHistoriqueResponseDTO mapToResponseDTO(LieuHistoriqueModel lieu) {
        String villeNom = lieu.getNomVille();
        String regionNom = lieu.getRegion();
        Long villeId = null;

        if (lieu.getVille() != null) {
            villeId = lieu.getVille().getIdVille();
            if (villeNom == null || villeNom.isBlank()) {
                villeNom = lieu.getVille().getNomVille();
            }
            if (regionNom == null || regionNom.isBlank()) {
                regionNom = lieu.getVille().getRegion();
                if ((regionNom == null || regionNom.isBlank()) && lieu.getVille().getRegionParent() != null) {
                    regionNom = lieu.getVille().getRegionParent().getNomRegion();
                }
            }
        }

        VilleSummaryDTO villeSummary = null;
        if (villeNom != null && !villeNom.isBlank()) {
            villeSummary = VilleSummaryDTO.builder()
                    .id(villeId)
                    .nom(villeNom)
                    .region(regionNom)
                    .build();
        }

        return LieuHistoriqueResponseDTO.builder()
                .idLieu(lieu.getIdLieu())
                .nomLieuHisto(lieu.getNomLieu())
                .description(lieu.getDescription())
                .epoque(lieu.getEpoque())
                .cordonnees(lieu.getCordonnees())
                .latitude(lieu.getLatitude())
                .longitude(lieu.getLongitude())
                .panorama360Url(lieu.getPanorama360Url())
                .nomVille(villeNom)
                .region(regionNom != null && !regionNom.isBlank() ? regionNom : "Mali")
                .ville(villeSummary)
                .build();
    }
}
