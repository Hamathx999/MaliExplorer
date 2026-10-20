package com.maliexplorer_backend.serviceimpl;

import com.maliexplorer_backend.dto.InfoPratiqueRequestDTO;
import com.maliexplorer_backend.dto.InfoPratiqueResponseDTO;
import com.maliexplorer_backend.exception.ResourceNotFoundException;
import com.maliexplorer_backend.model.InfoPratiqueModel;
import com.maliexplorer_backend.repository.InfoPratiqueRepository;
import com.maliexplorer_backend.service.InfoPratiqueService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implémentation du service métier de gestion des informations pratiques du voyageur.
 */
@Service
@RequiredArgsConstructor
public class InfoPratiqueServiceImpl implements InfoPratiqueService {

    private final InfoPratiqueRepository infoPratiqueRepository;

    @Override
    @Transactional(readOnly = true)
    public List<InfoPratiqueResponseDTO> getPublishedInfosPratiques() {
        return infoPratiqueRepository.findByPublieTrueOrderByOrdreAffichageAsc()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<InfoPratiqueResponseDTO> getAllInfosPratiques() {
        return infoPratiqueRepository.findAllByOrderByOrdreAffichageAsc()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<InfoPratiqueResponseDTO> getInfosPratiquesByCategorie(String categorie) {
        return infoPratiqueRepository.findByCategorieIgnoreCaseOrderByOrdreAffichageAsc(categorie)
                .stream()
                .filter(i -> Boolean.TRUE.equals(i.getPublie()))
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public InfoPratiqueResponseDTO getInfoPratiqueById(Long id) {
        InfoPratiqueModel info = infoPratiqueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Information pratique introuvable avec l'id : " + id));
        return mapToDTO(info);
    }

    @Override
    @Transactional
    public InfoPratiqueResponseDTO createInfoPratique(InfoPratiqueRequestDTO requestDTO) {
        InfoPratiqueModel info = InfoPratiqueModel.builder()
                .titre(requestDTO.getTitre().trim())
                .categorie(requestDTO.getCategorie().trim().toUpperCase())
                .description(requestDTO.getDescription())
                .conseils(requestDTO.getConseils())
                .coordonnees(requestDTO.getCoordonnees())
                .badge(requestDTO.getBadge())
                .badgeColor(requestDTO.getBadgeColor())
                .icon(requestDTO.getIcon())
                .imageUrl(requestDTO.getImageUrl())
                .ordreAffichage(requestDTO.getOrdreAffichage() != null ? requestDTO.getOrdreAffichage() : 0)
                .publie(requestDTO.getPublie() != null ? requestDTO.getPublie() : true)
                .build();

        return mapToDTO(infoPratiqueRepository.save(info));
    }

    @Override
    @Transactional
    public InfoPratiqueResponseDTO updateInfoPratique(Long id, InfoPratiqueRequestDTO requestDTO) {
        InfoPratiqueModel info = infoPratiqueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Information pratique introuvable avec l'id : " + id));

        if (requestDTO.getTitre() != null && !requestDTO.getTitre().isBlank()) {
            info.setTitre(requestDTO.getTitre().trim());
        }
        if (requestDTO.getCategorie() != null && !requestDTO.getCategorie().isBlank()) {
            info.setCategorie(requestDTO.getCategorie().trim().toUpperCase());
        }
        if (requestDTO.getDescription() != null) {
            info.setDescription(requestDTO.getDescription());
        }
        if (requestDTO.getConseils() != null) {
            info.setConseils(requestDTO.getConseils());
        }
        if (requestDTO.getCoordonnees() != null) {
            info.setCoordonnees(requestDTO.getCoordonnees());
        }
        if (requestDTO.getBadge() != null) {
            info.setBadge(requestDTO.getBadge());
        }
        if (requestDTO.getBadgeColor() != null) {
            info.setBadgeColor(requestDTO.getBadgeColor());
        }
        if (requestDTO.getIcon() != null) {
            info.setIcon(requestDTO.getIcon());
        }
        if (requestDTO.getImageUrl() != null) {
            info.setImageUrl(requestDTO.getImageUrl());
        }
        if (requestDTO.getOrdreAffichage() != null) {
            info.setOrdreAffichage(requestDTO.getOrdreAffichage());
        }
        if (requestDTO.getPublie() != null) {
            info.setPublie(requestDTO.getPublie());
        }

        return mapToDTO(infoPratiqueRepository.save(info));
    }

    @Override
    @Transactional
    public void deleteInfoPratique(Long id) {
        if (!infoPratiqueRepository.existsById(id)) {
            throw new ResourceNotFoundException("Information pratique introuvable avec l'id : " + id);
        }
        infoPratiqueRepository.deleteById(id);
    }

    @Override
    @Transactional
    public InfoPratiqueResponseDTO togglePublication(Long id) {
        InfoPratiqueModel info = infoPratiqueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Information pratique introuvable avec l'id : " + id));

        info.setPublie(info.getPublie() == null || !info.getPublie());
        return mapToDTO(infoPratiqueRepository.save(info));
    }

    /**
     * Convertit une entité InfoPratiqueModel en InfoPratiqueResponseDTO.
     */
    private InfoPratiqueResponseDTO mapToDTO(InfoPratiqueModel model) {
        return InfoPratiqueResponseDTO.builder()
                .id(model.getId())
                .titre(model.getTitre())
                .categorie(model.getCategorie())
                .description(model.getDescription())
                .conseils(model.getConseils())
                .coordonnees(model.getCoordonnees())
                .badge(model.getBadge())
                .badgeColor(model.getBadgeColor())
                .icon(model.getIcon())
                .imageUrl(model.getImageUrl())
                .ordreAffichage(model.getOrdreAffichage())
                .publie(model.getPublie())
                .dateCreation(model.getDateCreation())
                .dateModification(model.getDateModification())
                .build();
    }
}
