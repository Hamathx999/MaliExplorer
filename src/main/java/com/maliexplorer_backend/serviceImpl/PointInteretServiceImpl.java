package com.maliexplorer_backend.serviceImpl;

import com.maliexplorer_backend.dto.PointInteretRequestDTO;
import com.maliexplorer_backend.dto.PointInteretResponseDTO;
import com.maliexplorer_backend.exception.ResourceNotFoundException;
import com.maliexplorer_backend.model.PointInteret;
import com.maliexplorer_backend.repository.PointInteretRepository;
import com.maliexplorer_backend.service.PointInteretService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PointInteretServiceImpl implements PointInteretService {

    private final PointInteretRepository pointInteretRepository;

    @Override
    @Transactional(readOnly = true)
    public List<PointInteretResponseDTO> getAllPoints() {
        return pointInteretRepository.findAll()
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PointInteretResponseDTO getPointById(Long id) {
        PointInteret poi = findPointOrThrow(id);
        return mapToResponseDTO(poi);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PointInteretResponseDTO> getPointsByRegion(Long regionId) {
        return pointInteretRepository.findByRegionId(regionId)
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PointInteretResponseDTO> getPointsByType(String type) {
        return pointInteretRepository.findByTypeIgnoreCase(type)
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public PointInteretResponseDTO createPoint(PointInteretRequestDTO requestDTO) {
        PointInteret poi = PointInteret.builder()
                .nom(requestDTO.getNom())
                .type(requestDTO.getType())
                .latitude(requestDTO.getLatitude())
                .longitude(requestDTO.getLongitude())
                .description(requestDTO.getDescription())
                .imageUrl(requestDTO.getImageUrl())
                .panorama360Url(requestDTO.getPanorama360Url())
                .regionId(requestDTO.getRegionId())
                .build();

        PointInteret saved = pointInteretRepository.save(poi);
        return mapToResponseDTO(saved);
    }

    @Override
    public PointInteretResponseDTO updatePoint(Long id, PointInteretRequestDTO requestDTO) {
        PointInteret poi = findPointOrThrow(id);

        poi.setNom(requestDTO.getNom());
        poi.setType(requestDTO.getType());
        poi.setLatitude(requestDTO.getLatitude());
        poi.setLongitude(requestDTO.getLongitude());
        poi.setDescription(requestDTO.getDescription());
        poi.setImageUrl(requestDTO.getImageUrl());
        poi.setPanorama360Url(requestDTO.getPanorama360Url());
        poi.setRegionId(requestDTO.getRegionId());

        PointInteret updated = pointInteretRepository.save(poi);
        return mapToResponseDTO(updated);
    }

    @Override
    public void deletePoint(Long id) {
        PointInteret poi = findPointOrThrow(id);
        pointInteretRepository.delete(poi);
    }

    private PointInteret findPointOrThrow(Long id) {
        return pointInteretRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Point d'intérêt introuvable avec l'ID : " + id));
    }

    private PointInteretResponseDTO mapToResponseDTO(PointInteret poi) {
        return PointInteretResponseDTO.builder()
                .id(poi.getId())
                .nom(poi.getNom())
                .type(poi.getType())
                .latitude(poi.getLatitude())
                .longitude(poi.getLongitude())
                .description(poi.getDescription())
                .imageUrl(poi.getImageUrl())
                .panorama360Url(poi.getPanorama360Url())
                .regionId(poi.getRegionId())
                .build();
    }
}
