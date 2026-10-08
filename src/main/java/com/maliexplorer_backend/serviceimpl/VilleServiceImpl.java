package com.maliexplorer_backend.serviceimpl;

import com.maliexplorer_backend.dto.VilleRequestDTO;
import com.maliexplorer_backend.dto.VilleResponseDTO;
import com.maliexplorer_backend.exception.ResourceNotFoundException;
import com.maliexplorer_backend.model.RegionModel;
import com.maliexplorer_backend.model.VilleModel;
import com.maliexplorer_backend.dto.RegionSummaryDTO;
import com.maliexplorer_backend.dto.LieuHistoriqueSummaryDTO;
import com.maliexplorer_backend.repository.RegionRepository;
import com.maliexplorer_backend.repository.VilleRepository;
import com.maliexplorer_backend.service.VilleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class VilleServiceImpl implements VilleService {

    private final VilleRepository villeRepository;
    private final RegionRepository regionRepository;

    @Override
    @Transactional(readOnly = true)
    public List<VilleResponseDTO> getAllVilles() {
        return villeRepository.findAll()
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public VilleResponseDTO getVilleById(Long id) {
        VilleModel ville = findVilleOrThrow(id);
        return mapToResponseDTO(ville);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VilleResponseDTO> getVillesByRegion(Long regionId) {
        return villeRepository.findByRegionParentIdRegion(regionId)
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public VilleResponseDTO createVille(VilleRequestDTO requestDTO) {
        RegionModel regionParent = null;
        if (requestDTO.getRegionId() != null) {
            regionParent = regionRepository.findById(requestDTO.getRegionId()).orElse(null);
        }
        if (regionParent == null && StringUtils.hasText(requestDTO.getRegion())) {
            regionParent = regionRepository.findByNomRegionIgnoreCase(requestDTO.getRegion().trim()).orElse(null);
        }

        VilleModel ville = VilleModel.builder()
                .nomVille(requestDTO.getNom())
                .region(requestDTO.getRegion() != null ? requestDTO.getRegion() : (regionParent != null ? regionParent.getNomRegion() : "Mali"))
                .nbreHbt(requestDTO.getNbreHbt())
                .description(requestDTO.getDescription())
                .cordonnees(requestDTO.getCordonnees())
                .regionParent(regionParent)
                .build();

        VilleModel saved = villeRepository.save(ville);
        return mapToResponseDTO(saved);
    }

    @Override
    public VilleResponseDTO updateVille(Long id, VilleRequestDTO requestDTO) {
        VilleModel ville = findVilleOrThrow(id);

        RegionModel regionParent = null;
        if (requestDTO.getRegionId() != null) {
            regionParent = regionRepository.findById(requestDTO.getRegionId()).orElse(null);
        }
        if (regionParent == null && StringUtils.hasText(requestDTO.getRegion())) {
            regionParent = regionRepository.findByNomRegionIgnoreCase(requestDTO.getRegion().trim()).orElse(null);
        }
        if (regionParent != null) {
            ville.setRegionParent(regionParent);
        }

        ville.setNomVille(requestDTO.getNom());
        ville.setRegion(requestDTO.getRegion());
        ville.setNbreHbt(requestDTO.getNbreHbt());
        ville.setDescription(requestDTO.getDescription());
        ville.setCordonnees(requestDTO.getCordonnees());

        VilleModel updated = villeRepository.save(ville);
        return mapToResponseDTO(updated);
    }

    @Override
    public void deleteVille(Long id) {
        VilleModel ville = findVilleOrThrow(id);
        villeRepository.delete(ville);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VilleResponseDTO> searchVilles(String keyword) {
        return villeRepository.findByNomVilleContainingIgnoreCase(keyword)
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    private VilleModel findVilleOrThrow(Long id) {
        return villeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("VilleModel introuvable avec l'ID : " + id));
    }

    private VilleResponseDTO mapToResponseDTO(VilleModel ville) {
        RegionSummaryDTO regionSummary = null;
        if (ville.getRegionParent() != null) {
            regionSummary = RegionSummaryDTO.builder()
                    .id(ville.getRegionParent().getIdRegion())
                    .nom(ville.getRegionParent().getNomRegion())
                    .build();
        }

        List<LieuHistoriqueSummaryDTO> lieuxHistoriques = ville.getLieuxHistoriques() == null
                ? new java.util.ArrayList<>()
                : ville.getLieuxHistoriques().stream()
                        .map(l -> LieuHistoriqueSummaryDTO.builder()
                                .idLieu(l.getIdLieu())
                                .nomLieuHisto(l.getNomLieu())
                                .epoque(l.getEpoque())
                                .build())
                        .collect(Collectors.toList());

        return VilleResponseDTO.builder()
                .id(ville.getIdVille())
                .nom(ville.getNomVille())
                .region(ville.getRegion())
                .nbreHbt(ville.getNbreHbt())
                .description(ville.getDescription())
                .cordonnees(ville.getCordonnees())
                .regionParent(regionSummary)
                .lieuxHistoriques(lieuxHistoriques)
                .build();
    }
}
