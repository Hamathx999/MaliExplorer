package com.maliexplorer_backend.serviceImpl;

import com.maliexplorer_backend.dto.VilleRequestDTO;
import com.maliexplorer_backend.dto.VilleResponseDTO;
import com.maliexplorer_backend.exception.ResourceNotFoundException;
import com.maliexplorer_backend.model.Region;
import com.maliexplorer_backend.model.Ville;
import com.maliexplorer_backend.repository.RegionRepository;
import com.maliexplorer_backend.repository.VilleRepository;
import com.maliexplorer_backend.service.VilleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        Ville ville = findVilleOrThrow(id);
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
        Region regionParent = null;
        if (requestDTO.getRegionId() != null) {
            regionParent = regionRepository.findById(requestDTO.getRegionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Région introuvable avec l'ID : " + requestDTO.getRegionId()));
        }

        Ville ville = Ville.builder()
                .nomVille(requestDTO.getNom())
                .region(requestDTO.getRegion())
                .nbreHbts(requestDTO.getNbreHbts())
                .description(requestDTO.getDescription())
                .cordonnees(requestDTO.getCordonnees())
                .latitude(requestDTO.getLatitude())
                .longitude(requestDTO.getLongitude())
                .estCapitale(requestDTO.getEstCapitale() != null ? requestDTO.getEstCapitale() : false)
                .imageUrl(requestDTO.getImageUrl())
                .idUsers(requestDTO.getIdUsers())
                .regionParent(regionParent)
                .build();

        Ville saved = villeRepository.save(ville);
        return mapToResponseDTO(saved);
    }

    @Override
    public VilleResponseDTO updateVille(Long id, VilleRequestDTO requestDTO) {
        Ville ville = findVilleOrThrow(id);

        if (requestDTO.getRegionId() != null) {
            Region regionParent = regionRepository.findById(requestDTO.getRegionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Région introuvable avec l'ID : " + requestDTO.getRegionId()));
            ville.setRegionParent(regionParent);
        } else {
            ville.setRegionParent(null);
        }

        ville.setNomVille(requestDTO.getNom());
        ville.setRegion(requestDTO.getRegion());
        ville.setNbreHbts(requestDTO.getNbreHbts());
        ville.setDescription(requestDTO.getDescription());
        ville.setCordonnees(requestDTO.getCordonnees());
        ville.setLatitude(requestDTO.getLatitude());
        ville.setLongitude(requestDTO.getLongitude());
        ville.setEstCapitale(requestDTO.getEstCapitale());
        ville.setImageUrl(requestDTO.getImageUrl());
        if (requestDTO.getIdUsers() != null) {
            ville.setIdUsers(requestDTO.getIdUsers());
        }

        Ville updated = villeRepository.save(ville);
        return mapToResponseDTO(updated);
    }

    @Override
    public void deleteVille(Long id) {
        Ville ville = findVilleOrThrow(id);
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

    private Ville findVilleOrThrow(Long id) {
        return villeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ville introuvable avec l'ID : " + id));
    }

    private VilleResponseDTO mapToResponseDTO(Ville ville) {
        RegionSummaryDTO regionSummary = null;
        if (ville.getRegionParent() != null) {
            regionSummary = RegionSummaryDTO.builder()
                    .id(ville.getRegionParent().getIdRegion())
                    .nom(ville.getRegionParent().getNomRegion())
                    .code(ville.getRegionParent().getCode())
                    .imageUrl(ville.getRegionParent().getImageUrl())
                    .build();
        }

        List<LieuHistoriqueSummaryDTO> lieux = ville.getLieuxHistoriques() == null ? java.util.Collections.emptyList() :
                ville.getLieuxHistoriques().stream()
                        .map(l -> LieuHistoriqueSummaryDTO.builder()
                                .idLieu(l.getIdLieu())
                                .nomHistoire(l.getNomHistoire())
                                .epoque(l.getEpoque())
                                .imageUrl(l.getImageUrl())
                                .latitude(l.getLatitude())
                                .longitude(l.getLongitude())
                                .build())
                        .collect(java.util.stream.Collectors.toList());

        return VilleResponseDTO.builder()
                .id(ville.getIdVille())
                .nom(ville.getNomVille())
                .region(ville.getRegion())
                .nbreHbts(ville.getNbreHbts())
                .description(ville.getDescription())
                .cordonnees(ville.getCordonnees())
                .latitude(ville.getLatitude())
                .longitude(ville.getLongitude())
                .estCapitale(ville.getEstCapitale())
                .imageUrl(ville.getImageUrl())
                .idUsers(ville.getIdUsers())
                .regionParent(regionSummary)
                .lieuxHistoriques(lieux)
                .build();
    }
}
