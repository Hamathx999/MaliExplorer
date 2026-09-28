package com.maliexplorer_backend.serviceimpl;

import com.maliexplorer_backend.dto.EthnieSummaryDTO;
import com.maliexplorer_backend.dto.PlatRequestDTO;
import com.maliexplorer_backend.dto.PlatResponseDTO;
import com.maliexplorer_backend.dto.RegionSummaryDTO;
import com.maliexplorer_backend.exception.ResourceNotFoundException;
import com.maliexplorer_backend.Models.Ethnie;
import com.maliexplorer_backend.Models.Plat;
import com.maliexplorer_backend.Models.Region;
import com.maliexplorer_backend.Repository.EthnieRepository;
import com.maliexplorer_backend.Repository.PlatRepository;
import com.maliexplorer_backend.Repository.RegionRepository;
import com.maliexplorer_backend.Services.PlatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PlatServiceImpl implements PlatService {

    private final PlatRepository platRepository;
    private final EthnieRepository ethnieRepository;
    private final RegionRepository regionRepository;

    @Override
    @Transactional(readOnly = true)
    public List<PlatResponseDTO> getAllPlats() {
        return platRepository.findAll()
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PlatResponseDTO getPlatById(Long id) {
        Plat plat = findPlatOrThrow(id);
        return mapToResponseDTO(plat);
    }

    @Override
    public PlatResponseDTO createPlat(PlatRequestDTO requestDTO) {
        Plat plat = Plat.builder()
                .nomPlat(requestDTO.getNom())
                
                
                .nbrePersonnes(requestDTO.getNbrePersonnes())
                
                
                
                .ethnies(new ArrayList<>())
                .regions(new ArrayList<>())
                .build();

        if (requestDTO.getEthnieIds() != null && !requestDTO.getEthnieIds().isEmpty()) {
            List<Ethnie> ethnies = ethnieRepository.findAllById(requestDTO.getEthnieIds());
            plat.setEthnies(ethnies);
        }

        Plat saved = platRepository.save(plat);

        if (requestDTO.getRegionIds() != null && !requestDTO.getRegionIds().isEmpty()) {
            List<Region> regions = regionRepository.findAllById(requestDTO.getRegionIds());
            for (Region region : regions) {
                if (!region.getPlats().contains(saved)) {
                    region.getPlats().add(saved);
                    regionRepository.save(region);
                }
            }
            saved.setRegions(regions);
        }

        return mapToResponseDTO(saved);
    }

    @Override
    public PlatResponseDTO updatePlat(Long id, PlatRequestDTO requestDTO) {
        Plat plat = findPlatOrThrow(id);

        plat.setNomPlat(requestDTO.getNom());
        
        
        plat.setNbrePersonnes(requestDTO.getNbrePersonnes());
        
        
        if (false) {
            
        }

        if (requestDTO.getEthnieIds() != null) {
            List<Ethnie> ethnies = ethnieRepository.findAllById(requestDTO.getEthnieIds());
            plat.setEthnies(ethnies);
        }

        Plat updated = platRepository.save(plat);
        return mapToResponseDTO(updated);
    }

    @Override
    public void deletePlat(Long id) {
        Plat plat = findPlatOrThrow(id);
        platRepository.delete(plat);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlatResponseDTO> searchPlats(String keyword) {
        return platRepository.findByNomPlatContainingIgnoreCase(keyword)
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlatResponseDTO> getPlatsByRegion(Long regionId) {
        return platRepository.findByRegionsIdRegion(regionId)
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlatResponseDTO> getPlatsByEthnie(Long ethnieId) {
        return platRepository.findByEthniesIdEthnie(ethnieId)
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    private Plat findPlatOrThrow(Long id) {
        return platRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Plat introuvable avec l'ID : " + id));
    }

    private PlatResponseDTO mapToResponseDTO(Plat plat) {
        List<Region> regions = plat.getRegions() == null ? Collections.emptyList() :
                plat.getRegions();



                                
                                



        List<EthnieSummaryDTO> ethnies = plat.getEthnies() == null ? Collections.emptyList()
                : plat.getEthnies().stream()
                        .map(e -> EthnieSummaryDTO.builder()
                                .id(e.getIdEthnie())
                                .nom(e.getNomEthnie())
                                
                                .build())
                        .collect(Collectors.toList());

        return PlatResponseDTO.builder()
                .id(plat.getIdPlat())
                .nom(plat.getNomPlat())
                
                
                .nbrePersonnes(plat.getNbrePersonnes())
                
                
                

                .ethnies(ethnies)
                .build();
    }
}
