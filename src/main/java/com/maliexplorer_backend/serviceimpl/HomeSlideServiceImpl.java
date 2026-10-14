package com.maliexplorer_backend.serviceimpl;

import com.maliexplorer_backend.dto.HomeSlideRequestDTO;
import com.maliexplorer_backend.dto.HomeSlideResponseDTO;
import com.maliexplorer_backend.exception.ResourceNotFoundException;
import com.maliexplorer_backend.model.HomeSlideModel;
import com.maliexplorer_backend.repository.HomeSlideRepository;
import com.maliexplorer_backend.service.HomeSlideService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HomeSlideServiceImpl implements HomeSlideService {

    private final HomeSlideRepository homeSlideRepository;

    @Override
    @Transactional(readOnly = true)
    public List<HomeSlideResponseDTO> getActiveSlides() {
        return homeSlideRepository.findByActifTrueOrderByPrioritaireDescOrdreAscIdAsc()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<HomeSlideResponseDTO> getAllSlides() {
        return homeSlideRepository.findAllByOrderByOrdreAscIdAsc()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public HomeSlideResponseDTO getSlideById(Long id) {
        HomeSlideModel slide = homeSlideRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Slide introuvable avec l'id : " + id));
        return mapToDTO(slide);
    }

    @Override
    @Transactional
    public HomeSlideResponseDTO createSlide(HomeSlideRequestDTO requestDTO) {
        if (Boolean.TRUE.equals(requestDTO.getPrioritaire())) {
            // Désactiver la priorité des autres slides si celui-ci devient prioritaire
            unsetOtherPriorities();
        }

        HomeSlideModel slide = HomeSlideModel.builder()
                .titre(requestDTO.getTitre())
                .sousTitre(requestDTO.getSousTitre())
                .legende(requestDTO.getLegende())
                .imageUrl(requestDTO.getImageUrl())
                .lienAction(requestDTO.getLienAction())
                .ordre(requestDTO.getOrdre() != null ? requestDTO.getOrdre() : 0)
                .actif(requestDTO.getActif() != null ? requestDTO.getActif() : true)
                .prioritaire(requestDTO.getPrioritaire() != null ? requestDTO.getPrioritaire() : false)
                .build();

        return mapToDTO(homeSlideRepository.save(slide));
    }

    @Override
    @Transactional
    public HomeSlideResponseDTO updateSlide(Long id, HomeSlideRequestDTO requestDTO) {
        HomeSlideModel slide = homeSlideRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Slide introuvable avec l'id : " + id));

        if (Boolean.TRUE.equals(requestDTO.getPrioritaire())) {
            unsetOtherPriorities();
        }

        slide.setTitre(requestDTO.getTitre());
        slide.setSousTitre(requestDTO.getSousTitre());
        slide.setLegende(requestDTO.getLegende());
        slide.setImageUrl(requestDTO.getImageUrl());
        slide.setLienAction(requestDTO.getLienAction());
        if (requestDTO.getOrdre() != null) slide.setOrdre(requestDTO.getOrdre());
        if (requestDTO.getActif() != null) slide.setActif(requestDTO.getActif());
        if (requestDTO.getPrioritaire() != null) slide.setPrioritaire(requestDTO.getPrioritaire());

        return mapToDTO(homeSlideRepository.save(slide));
    }

    @Override
    @Transactional
    public void deleteSlide(Long id) {
        if (!homeSlideRepository.existsById(id)) {
            throw new ResourceNotFoundException("Slide introuvable avec l'id : " + id);
        }
        homeSlideRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void reorderSlides(List<Long> slideIds) {
        for (int i = 0; i < slideIds.size(); i++) {
            Long slideId = slideIds.get(i);
            int order = i + 1;
            homeSlideRepository.findById(slideId).ifPresent(s -> {
                s.setOrdre(order);
                homeSlideRepository.save(s);
            });
        }
    }

    private void unsetOtherPriorities() {
        List<HomeSlideModel> all = homeSlideRepository.findAll();
        for (HomeSlideModel s : all) {
            if (Boolean.TRUE.equals(s.getPrioritaire())) {
                s.setPrioritaire(false);
                homeSlideRepository.save(s);
            }
        }
    }

    private HomeSlideResponseDTO mapToDTO(HomeSlideModel model) {
        return HomeSlideResponseDTO.builder()
                .id(model.getId())
                .titre(model.getTitre())
                .sousTitre(model.getSousTitre())
                .legende(model.getLegende())
                .imageUrl(model.getImageUrl())
                .lienAction(model.getLienAction())
                .ordre(model.getOrdre())
                .actif(model.getActif())
                .prioritaire(model.getPrioritaire())
                .dateCreation(model.getDateCreation())
                .dateModification(model.getDateModification())
                .build();
    }
}

