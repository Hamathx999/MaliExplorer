package com.maliexplorer_backend.serviceimpl;

import com.maliexplorer_backend.dto.*;
import com.maliexplorer_backend.exception.BadRequestException;
import com.maliexplorer_backend.exception.ResourceNotFoundException;
import com.maliexplorer_backend.model.RegionModel;
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
        private final jakarta.persistence.EntityManager entityManager;

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
                RegionModel region = findRegionOrThrow(id);
                return mapToResponseDTO(region);
        }

        @Override
        public RegionResponseDTO createRegion(RegionRequestDTO requestDTO) {
                if (regionRepository.existsByNomRegionIgnoreCase(requestDTO.getNom())) {
                        throw new BadRequestException(
                                        "Une région avec le nom '" + requestDTO.getNom() + "' existe déjà");
                }

                RegionModel region = RegionModel.builder()
                                .nomRegion(requestDTO.getNom())
                                .nbreHbt(requestDTO.getNbreHbt())
                                .description(requestDTO.getDescription())
                                .chefLieu(requestDTO.getChefLieu())
                                .superficie(requestDTO.getSuperficie())
                                .latitude(requestDTO.getLatitude())
                                .longitude(requestDTO.getLongitude())
                                .imageUrl(requestDTO.getImageUrl())
                                .build();

                RegionModel saved = regionRepository.save(region);
                return mapToResponseDTO(saved);
        }

        @Override
        public RegionResponseDTO updateRegion(Long id, RegionRequestDTO requestDTO) {
                RegionModel region = findRegionOrThrow(id);

                if (!region.getNomRegion().equalsIgnoreCase(requestDTO.getNom())
                                && regionRepository.existsByNomRegionIgnoreCase(requestDTO.getNom())) {
                        throw new BadRequestException(
                                        "Une région avec le nom '" + requestDTO.getNom() + "' existe déjà");
                }

                region.setNomRegion(requestDTO.getNom());
                if (requestDTO.getNbreHbt() != null) region.setNbreHbt(requestDTO.getNbreHbt());
                if (requestDTO.getDescription() != null) region.setDescription(requestDTO.getDescription());
                if (requestDTO.getChefLieu() != null) region.setChefLieu(requestDTO.getChefLieu());
                if (requestDTO.getSuperficie() != null) region.setSuperficie(requestDTO.getSuperficie());
                if (requestDTO.getLatitude() != null) region.setLatitude(requestDTO.getLatitude());
                if (requestDTO.getLongitude() != null) region.setLongitude(requestDTO.getLongitude());
                if (requestDTO.getImageUrl() != null) region.setImageUrl(requestDTO.getImageUrl());

                RegionModel updated = regionRepository.save(region);
                return mapToResponseDTO(updated);
        }

        @Override
        public void deleteRegion(Long id) {
                RegionModel region = findRegionOrThrow(id);

                try {
                        entityManager.createNativeQuery("DELETE FROM region_ethnie WHERE region_id = :id")
                                        .setParameter("id", id)
                                        .executeUpdate();
                } catch (Exception e) {}

                try {
                        entityManager.createNativeQuery("DELETE FROM region_plat WHERE region_id = :id")
                                        .setParameter("id", id)
                                        .executeUpdate();
                } catch (Exception e) {}

                try {
                        entityManager.createNativeQuery("DELETE f FROM favoris f INNER JOIN lieux_historiques l ON f.id_lieu = l.id_lieu INNER JOIN villes v ON l.id_ville = v.id_ville WHERE v.id_region = :id")
                                        .setParameter("id", id)
                                        .executeUpdate();
                } catch (Exception e) {}

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

        private RegionModel findRegionOrThrow(Long id) {
                return regionRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Région introuvable avec l'ID : " + id));
        }

        private RegionResponseDTO mapToResponseDTO(RegionModel region) {
                List<VilleSummaryDTO> villes = region.getVilles() == null ? Collections.emptyList()
                                : region.getVilles().stream()
                                                .map(v -> VilleSummaryDTO.builder()
                                                                .id(v.getIdVille())
                                                                .nom(v.getNomVille())
                                                                .build())
                                                .collect(Collectors.toList());

                List<EthnieSummaryDTO> ethnies = region.getEthnies() == null ? Collections.emptyList()
                                : region.getEthnies().stream()
                                                .map(e -> EthnieSummaryDTO.builder()
                                                                .id(e.getIdEthnie())
                                                                .nom(e.getNomEthnie())
                                                                .build())
                                                .collect(Collectors.toList());

                List<PlatSummaryDTO> plats = region.getPlats() == null ? Collections.emptyList()
                                : region.getPlats().stream()
                                                .map(p -> PlatSummaryDTO.builder()
                                                                .id(p.getIdPlat())
                                                                .nom(p.getNomPlat())
                                                                .build())
                                                .collect(Collectors.toList());

                return RegionResponseDTO.builder()
                                .id(region.getIdRegion())
                                .nom(region.getNomRegion())
                                .description(region.getDescription())
                                .chefLieu(region.getChefLieu())
                                .superficie(region.getSuperficie())
                                .latitude(region.getLatitude())
                                .longitude(region.getLongitude())
                                .imageUrl(region.getImageUrl())
                                .nbreHbt(region.getNbreHbt())
                                .population(region.getNbreHbt())
                                .villes(villes)
                                .ethnies(ethnies)
                                .plats(plats)
                                .build();
        }
}
