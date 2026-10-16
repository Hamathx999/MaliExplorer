package com.maliexplorer_backend.serviceimpl;

import com.maliexplorer_backend.dto.ReligionRequestDTO;
import com.maliexplorer_backend.dto.ReligionResponseDTO;
import com.maliexplorer_backend.exception.ResourceNotFoundException;
import com.maliexplorer_backend.model.ReligionModel;
import com.maliexplorer_backend.repository.ReligionRepository;
import com.maliexplorer_backend.service.ReligionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReligionServiceImpl implements ReligionService {

    private final ReligionRepository religionRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ReligionResponseDTO> getPublishedReligions() {
        return religionRepository.findByPublieTrueOrderByOrdreAscIdAsc()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReligionResponseDTO> getAllReligions() {
        return religionRepository.findAllByOrderByOrdreAscIdAsc()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ReligionResponseDTO getReligionById(Long id) {
        ReligionModel r = religionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fiche de religion introuvable avec l'id : " + id));
        return mapToDTO(r);
    }

    @Override
    @Transactional
    public ReligionResponseDTO createReligion(ReligionRequestDTO requestDTO) {
        String description = requestDTO.getDescription();
        String histoire = requestDTO.getHistoire();
        if ((histoire == null || histoire.isBlank()) && description != null) {
            histoire = description;
        }
        if ((description == null || description.isBlank()) && histoire != null) {
            description = histoire;
        }

        ReligionModel r = ReligionModel.builder()
                .nom(requestDTO.getNom())
                .categorie(requestDTO.getCategorie() != null ? requestDTO.getCategorie() : "Religion")
                .description(description)
                .histoire(histoire)
                .pratiques(requestDTO.getPratiques())
                .fetes(requestDTO.getFetes())
                .lieuxDeCulte(requestDTO.getLieuxDeCulte())
                .regionsConcernees(requestDTO.getRegionsConcernees())
                .branches(requestDTO.getBranches())
                .proportionPopulation(requestDTO.getProportionPopulation())
                .nombrePratiquantsEstime(requestDTO.getNombrePratiquantsEstime())
                .sourceStatistiques(requestDTO.getSourceStatistiques())
                .anneeStatistiques(requestDTO.getAnneeStatistiques())
                .methodologieStatistiques(requestDTO.getMethodologieStatistiques())
                .imageUrl(requestDTO.getImageUrl())
                .ordre(requestDTO.getOrdre() != null ? requestDTO.getOrdre() : 0)
                .publie(requestDTO.getPublie() != null ? requestDTO.getPublie() : true)
                .build();

        return mapToDTO(religionRepository.save(r));
    }

    @Override
    @Transactional
    public ReligionResponseDTO updateReligion(Long id, ReligionRequestDTO requestDTO) {
        ReligionModel r = religionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fiche de religion introuvable avec l'id : " + id));

        if (requestDTO.getNom() != null && !requestDTO.getNom().isBlank()) {
            r.setNom(requestDTO.getNom().trim());
        }
        if (requestDTO.getCategorie() != null && !requestDTO.getCategorie().isBlank()) {
            r.setCategorie(requestDTO.getCategorie());
        }
        if (requestDTO.getDescription() != null && !requestDTO.getDescription().isBlank()) {
            r.setDescription(requestDTO.getDescription());
        }
        if (requestDTO.getHistoire() != null && !requestDTO.getHistoire().isBlank()) {
            r.setHistoire(requestDTO.getHistoire());
        } else if (requestDTO.getDescription() != null && !requestDTO.getDescription().isBlank() && (r.getHistoire() == null || r.getHistoire().isBlank())) {
            r.setHistoire(requestDTO.getDescription());
        }
        if (r.getDescription() == null || r.getDescription().isBlank()) {
            if (r.getHistoire() != null && !r.getHistoire().isBlank()) {
                r.setDescription(r.getHistoire());
            }
        }
        if (requestDTO.getPratiques() != null && !requestDTO.getPratiques().isBlank()) {
            r.setPratiques(requestDTO.getPratiques());
        }
        if (requestDTO.getFetes() != null && !requestDTO.getFetes().isBlank()) {
            r.setFetes(requestDTO.getFetes());
        }
        if (requestDTO.getLieuxDeCulte() != null && !requestDTO.getLieuxDeCulte().isBlank()) {
            r.setLieuxDeCulte(requestDTO.getLieuxDeCulte());
        }
        if (requestDTO.getRegionsConcernees() != null && !requestDTO.getRegionsConcernees().isBlank()) {
            r.setRegionsConcernees(requestDTO.getRegionsConcernees());
        }
        if (requestDTO.getBranches() != null && !requestDTO.getBranches().isBlank()) {
            r.setBranches(requestDTO.getBranches());
        }
        if (requestDTO.getProportionPopulation() != null && !requestDTO.getProportionPopulation().isBlank()) {
            r.setProportionPopulation(requestDTO.getProportionPopulation());
        }
        if (requestDTO.getNombrePratiquantsEstime() != null && !requestDTO.getNombrePratiquantsEstime().isBlank()) {
            r.setNombrePratiquantsEstime(requestDTO.getNombrePratiquantsEstime());
        }
        if (requestDTO.getSourceStatistiques() != null && !requestDTO.getSourceStatistiques().isBlank()) {
            r.setSourceStatistiques(requestDTO.getSourceStatistiques());
        }
        if (requestDTO.getAnneeStatistiques() != null && !requestDTO.getAnneeStatistiques().isBlank()) {
            r.setAnneeStatistiques(requestDTO.getAnneeStatistiques());
        }
        if (requestDTO.getMethodologieStatistiques() != null && !requestDTO.getMethodologieStatistiques().isBlank()) {
            r.setMethodologieStatistiques(requestDTO.getMethodologieStatistiques());
        }
        if (requestDTO.getImageUrl() != null && !requestDTO.getImageUrl().isBlank()) {
            r.setImageUrl(requestDTO.getImageUrl());
        }
        if (requestDTO.getOrdre() != null) {
            r.setOrdre(requestDTO.getOrdre());
        }
        if (requestDTO.getPublie() != null) {
            r.setPublie(requestDTO.getPublie());
        }

        return mapToDTO(religionRepository.save(r));
    }

    @Transactional
    public ReligionResponseDTO togglePublication(Long id) {
        ReligionModel r = religionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fiche de religion introuvable avec l'id : " + id));
        r.setPublie(r.getPublie() == null || !r.getPublie());
        return mapToDTO(religionRepository.save(r));
    }

    @Override
    @Transactional
    public void deleteReligion(Long id) {
        if (!religionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Fiche de religion introuvable avec l'id : " + id);
        }
        religionRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReligionResponseDTO> searchReligions(String keyword) {
        return religionRepository.searchReligions(keyword)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private ReligionResponseDTO mapToDTO(ReligionModel r) {
        return ReligionResponseDTO.builder()
                .id(r.getId())
                .nom(r.getNom())
                .categorie(r.getCategorie())
                .description(r.getDescription())
                .histoire(r.getHistoire())
                .pratiques(r.getPratiques())
                .fetes(r.getFetes())
                .lieuxDeCulte(r.getLieuxDeCulte())
                .regionsConcernees(r.getRegionsConcernees())
                .branches(r.getBranches())
                .proportionPopulation(r.getProportionPopulation())
                .nombrePratiquantsEstime(r.getNombrePratiquantsEstime())
                .sourceStatistiques(r.getSourceStatistiques())
                .anneeStatistiques(r.getAnneeStatistiques())
                .methodologieStatistiques(r.getMethodologieStatistiques())
                .imageUrl(r.getImageUrl())
                .ordre(r.getOrdre())
                .publie(r.getPublie())
                .dateCreation(r.getDateCreation())
                .dateModification(r.getDateModification())
                .build();
    }
}
