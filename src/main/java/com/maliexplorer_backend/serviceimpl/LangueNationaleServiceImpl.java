package com.maliexplorer_backend.serviceimpl;

import com.maliexplorer_backend.dto.LangueRequestDTO;
import com.maliexplorer_backend.dto.LangueResponseDTO;
import com.maliexplorer_backend.exception.ResourceNotFoundException;
import com.maliexplorer_backend.model.LangueNationaleModel;
import com.maliexplorer_backend.repository.LangueNationaleRepository;
import com.maliexplorer_backend.service.LangueNationaleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implémentation du service métier de gestion des langues nationales.
 */
@Service
@RequiredArgsConstructor
public class LangueNationaleServiceImpl implements LangueNationaleService {

    private final LangueNationaleRepository langueRepository;

    @Override
    @Transactional(readOnly = true)
    public List<LangueResponseDTO> getPublishedLangues() {
        return langueRepository.findByPublieTrueOrderByOrdreAffichageAsc()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LangueResponseDTO> getAllLangues() {
        return langueRepository.findAllByOrderByOrdreAffichageAsc()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public LangueResponseDTO getLangueById(Long id) {
        LangueNationaleModel langue = langueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Langue nationale introuvable avec l'id : " + id));
        return mapToDTO(langue);
    }

    @Override
    @Transactional
    public LangueResponseDTO createLangue(LangueRequestDTO requestDTO) {
        LangueNationaleModel langue = LangueNationaleModel.builder()
                .nom(requestDTO.getNom().trim())
                .nomLocal(requestDTO.getNomLocal())
                .description(requestDTO.getDescription())
                .locuteurs(requestDTO.getLocuteurs())
                .regions(requestDTO.getRegions())
                .imageUrl(requestDTO.getImageUrl())
                .lexiqueJson(requestDTO.getLexiqueJson())
                .ordreAffichage(requestDTO.getOrdreAffichage() != null ? requestDTO.getOrdreAffichage() : 0)
                .publie(requestDTO.getPublie() != null ? requestDTO.getPublie() : true)
                .build();

        return mapToDTO(langueRepository.save(langue));
    }

    @Override
    @Transactional
    public LangueResponseDTO updateLangue(Long id, LangueRequestDTO requestDTO) {
        LangueNationaleModel langue = langueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Langue nationale introuvable avec l'id : " + id));

        if (requestDTO.getNom() != null && !requestDTO.getNom().isBlank()) {
            langue.setNom(requestDTO.getNom().trim());
        }
        if (requestDTO.getNomLocal() != null) {
            langue.setNomLocal(requestDTO.getNomLocal().trim());
        }
        if (requestDTO.getDescription() != null) {
            langue.setDescription(requestDTO.getDescription());
        }
        if (requestDTO.getLocuteurs() != null) {
            langue.setLocuteurs(requestDTO.getLocuteurs());
        }
        if (requestDTO.getRegions() != null) {
            langue.setRegions(requestDTO.getRegions());
        }
        if (requestDTO.getImageUrl() != null) {
            langue.setImageUrl(requestDTO.getImageUrl());
        }
        if (requestDTO.getLexiqueJson() != null) {
            langue.setLexiqueJson(requestDTO.getLexiqueJson());
        }
        if (requestDTO.getOrdreAffichage() != null) {
            langue.setOrdreAffichage(requestDTO.getOrdreAffichage());
        }
        if (requestDTO.getPublie() != null) {
            langue.setPublie(requestDTO.getPublie());
        }

        return mapToDTO(langueRepository.save(langue));
    }

    @Override
    @Transactional
    public void deleteLangue(Long id) {
        if (!langueRepository.existsById(id)) {
            throw new ResourceNotFoundException("Langue nationale introuvable avec l'id : " + id);
        }
        langueRepository.deleteById(id);
    }

    @Override
    @Transactional
    public LangueResponseDTO togglePublication(Long id) {
        LangueNationaleModel langue = langueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Langue nationale introuvable avec l'id : " + id));

        langue.setPublie(langue.getPublie() == null || !langue.getPublie());
        return mapToDTO(langueRepository.save(langue));
    }

    /**
     * Convertit une entité LangueNationaleModel en LangueResponseDTO.
     */
    private LangueResponseDTO mapToDTO(LangueNationaleModel model) {
        return LangueResponseDTO.builder()
                .id(model.getId())
                .nom(model.getNom())
                .nomLocal(model.getNomLocal())
                .description(model.getDescription())
                .locuteurs(model.getLocuteurs())
                .regions(model.getRegions())
                .imageUrl(model.getImageUrl())
                .lexiqueJson(model.getLexiqueJson())
                .ordreAffichage(model.getOrdreAffichage())
                .publie(model.getPublie())
                .dateCreation(model.getDateCreation())
                .dateModification(model.getDateModification())
                .build();
    }
}
