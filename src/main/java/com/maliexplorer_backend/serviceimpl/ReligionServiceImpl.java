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
        ReligionModel r = ReligionModel.builder()
                .nom(requestDTO.getNom())
                .categorie(requestDTO.getCategorie())
                .description(requestDTO.getDescription())
                .histoire(requestDTO.getHistoire())
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

        r.setNom(requestDTO.getNom());
        r.setCategorie(requestDTO.getCategorie());
        r.setDescription(requestDTO.getDescription());
        r.setHistoire(requestDTO.getHistoire());
        r.setPratiques(requestDTO.getPratiques());
        r.setFetes(requestDTO.getFetes());
        r.setLieuxDeCulte(requestDTO.getLieuxDeCulte());
        r.setRegionsConcernees(requestDTO.getRegionsConcernees());
        r.setBranches(requestDTO.getBranches());
        r.setProportionPopulation(requestDTO.getProportionPopulation());
        r.setNombrePratiquantsEstime(requestDTO.getNombrePratiquantsEstime());
        r.setSourceStatistiques(requestDTO.getSourceStatistiques());
        r.setAnneeStatistiques(requestDTO.getAnneeStatistiques());
        r.setMethodologieStatistiques(requestDTO.getMethodologieStatistiques());
        r.setImageUrl(requestDTO.getImageUrl());
        if (requestDTO.getOrdre() != null) r.setOrdre(requestDTO.getOrdre());
        if (requestDTO.getPublie() != null) r.setPublie(requestDTO.getPublie());

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
