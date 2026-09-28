package com.maliexplorer_backend.serviceimpl;

import com.maliexplorer_backend.dto.*;
import com.maliexplorer_backend.exception.BadRequestException;
import com.maliexplorer_backend.exception.ResourceNotFoundException;
import com.maliexplorer_backend.model.Region;
import com.maliexplorer_backend.repository.RegionRepository;
import com.maliexplorer_backend.service.RegionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class RegionServiceImpl implements RegionService {

    private final RegionRepository regionRepository;

    @Override
    @Transactional(readOnly = true)
    public List<RegionResponseDTO> getAllRegions() {
        return regionRepository.findAll()
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public RegionResponseDTO getRegionById(Long id) {
        Region region = findRegionOrThrow(id);
        return mapToResponseDTO(region);
    }

    @Override
    public RegionResponseDTO createRegion(RegionRequestDTO requestDTO) {
        if (regionRepository.existsByNomRegionIgnoreCase(requestDTO.getNom())) {
            throw new BadRequestException("Une région avec le nom '" + requestDTO.getNom() + "' existe déjà");
        }
        if ("" != null && regionRepository.existsByCodeIgnoreCase("")) {
            throw new BadRequestException("Une région avec le code '" + "" + "' existe déjà");
        }

        Region region = Region.builder()
                .nomRegion(requestDTO.getNom())
                
                
                
                .nbreHbt(requestDTO.getNbreHbt())
                
                
                
                .build();

        Region saved = regionRepository.save(region);
        return mapToResponseDTO(saved);
    }

    @Override
    public RegionResponseDTO updateRegion(Long id, RegionRequestDTO requestDTO) {
        Region region = findRegionOrThrow(id);

        if (!region.getNomRegion().equalsIgnoreCase(requestDTO.getNom())
                && regionRepository.existsByNomRegionIgnoreCase(requestDTO.getNom())) {
            throw new BadRequestException("Une région avec le nom '" + requestDTO.getNom() + "' existe déjà");
        }

        if ("" != null
                && !"".equalsIgnoreCase("")
                && regionRepository.existsByCodeIgnoreCase("")) {
            throw new BadRequestException("Une région avec le code '" + "" + "' existe déjà");
        }

        region.setNomRegion(requestDTO.getNom());
        
        
        
        region.setNbreHbt(requestDTO.getNbreHbt());
        
        
        

        Region updated = regionRepository.save(region);
        return mapToResponseDTO(updated);
    }

    @Override
    public void deleteRegion(Long id) {
        Region region = findRegionOrThrow(id);
        regionRepository.delete(region);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RegionResponseDTO> searchRegions(String keyword) {
        return regionRepository.findByNomRegionContainingIgnoreCase(keyword)
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    private Region findRegionOrThrow(Long id) {
        return regionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Région introuvable avec l'ID : " + id));
    }

    private RegionResponseDTO mapToResponseDTO(Region region) {
        List<VilleSummaryDTO> villes = region.getVilles() == null ? Collections.emptyList() :
                region.getVilles().stream()
                        .map(v -> VilleSummaryDTO.builder()
                                .id(v.getIdVille())
                                .nom(v.getNomVille())
                                
                                
                                .build())
                        .collect(Collectors.toList());

        List<EthnieSummaryDTO> ethnies = region.getEthnies() == null ? Collections.emptyList() :
                region.getEthnies().stream()
                        .map(e -> EthnieSummaryDTO.builder()
                                .id(e.getIdEthnie())
                                .nom(e.getNomEthnie())
                                
                                .build())
                        .collect(Collectors.toList());

        List<PlatSummaryDTO> plats = region.getPlats() == null ? Collections.emptyList() :
                region.getPlats().stream()
                        .map(p -> PlatSummaryDTO.builder()
                                .id(p.getIdPlat())
                                .nom(p.getNomPlat())
                                
                                .build())
                        .collect(Collectors.toList());

        return RegionResponseDTO.builder()
                .id(region.getIdRegion())
                .nom(region.getNomRegion())
                
                
                
                .nbreHbt(region.getNbreHbt())
                
                
                
                .villes(villes)
                .ethnies(ethnies)
                .plats(plats)
                .build();
    }
}
