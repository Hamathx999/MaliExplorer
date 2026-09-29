package com.maliexplorer_backend.serviceimpl;

import com.maliexplorer_backend.dto.EthnieRequestDTO;
import com.maliexplorer_backend.dto.EthnieResponseDTO;
import com.maliexplorer_backend.dto.PlatSummaryDTO;
import com.maliexplorer_backend.dto.RegionSummaryDTO;
import com.maliexplorer_backend.exception.BadRequestException;
import com.maliexplorer_backend.exception.ResourceNotFoundException;
import com.maliexplorer_backend.model.EthnieModel;
import com.maliexplorer_backend.model.RegionModel;
import com.maliexplorer_backend.repository.EthnieRepository;
import com.maliexplorer_backend.repository.RegionRepository;
import com.maliexplorer_backend.service.EthnieService;
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
public class EthnieServiceImpl implements EthnieService {

    private final EthnieRepository ethnieRepository;
    private final RegionRepository regionRepository;

    @Override
    @Transactional(readOnly = true)
    public org.springframework.data.domain.Page<EthnieResponseDTO> getAllEthnies(org.springframework.data.domain.Pageable pageable) {
        return ethnieRepository.findAll(pageable).map(this::mapToResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EthnieResponseDTO> getAllEthnies() {
        return ethnieRepository.findAll()
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public EthnieResponseDTO getEthnieById(Long id) {
        EthnieModel ethnie = findEthnieOrThrow(id);
        return mapToResponseDTO(ethnie);
    }

    @Override
    public EthnieResponseDTO createEthnie(EthnieRequestDTO requestDTO) {
        if (ethnieRepository.existsByNomEthnieIgnoreCase(requestDTO.getNom())) {
            throw new BadRequestException("Une ethnie avec le nom '" + requestDTO.getNom() + "' existe déjà");
        }

        EthnieModel ethnie = EthnieModel.builder()
                .nomEthnie(requestDTO.getNom())
                .region(requestDTO.getRegion())
                .population(requestDTO.getPopulation())
                .description(requestDTO.getDescription())
                .regions(new ArrayList<>())
                .plats(new ArrayList<>())
                .build();

        EthnieModel saved = ethnieRepository.save(ethnie);

        if (requestDTO.getRegionIds() != null && !requestDTO.getRegionIds().isEmpty()) {
            List<RegionModel> regions = regionRepository.findAllById(requestDTO.getRegionIds());
            for (RegionModel region : regions) {
                if (!region.getEthnies().contains(saved)) {
                    region.getEthnies().add(saved);
                    regionRepository.save(region);
                }
            }
            saved.setRegions(regions);
        }

        return mapToResponseDTO(saved);
    }

    @Override
    public EthnieResponseDTO updateEthnie(Long id, EthnieRequestDTO requestDTO) {
        EthnieModel ethnie = findEthnieOrThrow(id);

        if (!ethnie.getNomEthnie().equalsIgnoreCase(requestDTO.getNom())
                && ethnieRepository.existsByNomEthnieIgnoreCase(requestDTO.getNom())) {
            throw new BadRequestException("Une ethnie avec le nom '" + requestDTO.getNom() + "' existe déjà");
        }

        ethnie.setNomEthnie(requestDTO.getNom());
        ethnie.setRegion(requestDTO.getRegion());
        ethnie.setPopulation(requestDTO.getPopulation());
        ethnie.setDescription(requestDTO.getDescription());
        
        

        EthnieModel updated = ethnieRepository.save(ethnie);
        return mapToResponseDTO(updated);
    }

    @Override
    public void deleteEthnie(Long id) {
        EthnieModel ethnie = findEthnieOrThrow(id);
        ethnieRepository.delete(ethnie);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EthnieResponseDTO> searchEthnies(String keyword) {
        return ethnieRepository.findByNomEthnieContainingIgnoreCase(keyword)
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EthnieResponseDTO> getEthniesByRegion(Long regionId) {
        return ethnieRepository.findByRegionsIdRegion(regionId)
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    private EthnieModel findEthnieOrThrow(Long id) {
        return ethnieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("EthnieModel introuvable avec l'ID : " + id));
    }

    private EthnieResponseDTO mapToResponseDTO(EthnieModel ethnie) {
        List<RegionSummaryDTO> regions = ethnie.getRegions() == null ? Collections.emptyList() :
                new java.util.ArrayList<>();

        List<PlatSummaryDTO> plats = new java.util.ArrayList<>();

        return EthnieResponseDTO.builder()
                .id(ethnie.getIdEthnie())
                .nom(ethnie.getNomEthnie())
                .region(ethnie.getRegion())
                .population(ethnie.getPopulation())
                .description(ethnie.getDescription())
                .regions(regions)
                .plats(plats)
                .build();
    }
}

