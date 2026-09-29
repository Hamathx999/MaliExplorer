package com.maliexplorer_backend.serviceimpl;

import com.maliexplorer_backend.dto.PresidentRequestDTO;
import com.maliexplorer_backend.dto.PresidentResponseDTO;
import com.maliexplorer_backend.exception.ResourceNotFoundException;
import com.maliexplorer_backend.model.PresidentModel;
import com.maliexplorer_backend.repository.PresidentRepository;
import com.maliexplorer_backend.service.PresidentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PresidentServiceImpl implements PresidentService {

    private final PresidentRepository presidentRepository;

    @Override
    @Transactional(readOnly = true)
    public List<PresidentResponseDTO> getAllPresidents() {
        return presidentRepository.findAll()
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PresidentResponseDTO getPresidentById(Long id) {
        PresidentModel president = findPresidentOrThrow(id);
        return mapToResponseDTO(president);
    }

    @Override
    public PresidentResponseDTO createPresident(PresidentRequestDTO requestDTO) {
        PresidentModel president = PresidentModel.builder()
                .prenom(requestDTO.getPrenom())
                .nom(requestDTO.getNom())
                .dateNaissance(requestDTO.getDateNaissance())
                .dateDeces(requestDTO.getDateDeces())
                .periodeMandat(requestDTO.getPeriodeMandat())
                .biographie(requestDTO.getBiographie())
                
                .photoUrl(requestDTO.getPhotoUrl())
                
                .build();

        PresidentModel saved = presidentRepository.save(president);
        return mapToResponseDTO(saved);
    }

    @Override
    public PresidentResponseDTO updatePresident(Long id, PresidentRequestDTO requestDTO) {
        PresidentModel president = findPresidentOrThrow(id);

        president.setPrenom(requestDTO.getPrenom());
        president.setNom(requestDTO.getNom());
        president.setDateNaissance(requestDTO.getDateNaissance());
        president.setDateDeces(requestDTO.getDateDeces());
        president.setPeriodeMandat(requestDTO.getPeriodeMandat());
        president.setBiographie(requestDTO.getBiographie());
        
        president.setPhotoUrl(requestDTO.getPhotoUrl());
        

        PresidentModel updated = presidentRepository.save(president);
        return mapToResponseDTO(updated);
    }

    @Override
    public void deletePresident(Long id) {
        PresidentModel president = findPresidentOrThrow(id);
        presidentRepository.delete(president);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PresidentResponseDTO> searchPresidents(String keyword) {
        return presidentRepository.findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCase(keyword, keyword)
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    private PresidentModel findPresidentOrThrow(Long id) {
        return presidentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Président introuvable avec l'ID : " + id));
    }

    private PresidentResponseDTO mapToResponseDTO(PresidentModel president) {
        return PresidentResponseDTO.builder()
                .id(president.getIdPresident())
                .prenom(president.getPrenom())
                .nom(president.getNom())
                .dateNaissance(president.getDateNaissance())
                .dateDeces(president.getDateDeces())
                .periodeMandat(president.getPeriodeMandat())
                .biographie(president.getBiographie())
                
                .photoUrl(president.getPhotoUrl())
                
                .build();
    }
}
