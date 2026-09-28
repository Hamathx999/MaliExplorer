package com.maliexplorer_backend.serviceimpl;

import com.maliexplorer_backend.dto.LieuHistoriqueRequestDTO;
import com.maliexplorer_backend.dto.LieuHistoriqueResponseDTO;
import com.maliexplorer_backend.dto.VilleSummaryDTO;
import com.maliexplorer_backend.exception.ResourceNotFoundException;
import com.maliexplorer_backend.Models.LieuHistorique;
import com.maliexplorer_backend.Models.Ville;
import com.maliexplorer_backend.Repository.LieuHistoriqueRepository;
import com.maliexplorer_backend.Repository.VilleRepository;
import com.maliexplorer_backend.Services.LieuHistoriqueService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class LieuHistoriqueServiceImpl implements LieuHistoriqueService {

    private final LieuHistoriqueRepository lieuHistoriqueRepository;
    private final VilleRepository villeRepository;

    @Override
    @Transactional(readOnly = true)
    public List<LieuHistoriqueResponseDTO> getAllLieux() {
        return lieuHistoriqueRepository.findAll()
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public LieuHistoriqueResponseDTO getLieuById(Long id) {
        LieuHistorique lieu = findLieuOrThrow(id);
        return mapToResponseDTO(lieu);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LieuHistoriqueResponseDTO> getLieuxByVille(Long villeId) {
        return lieuHistoriqueRepository.findByVilleIdVille(villeId)
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public LieuHistoriqueResponseDTO createLieu(LieuHistoriqueRequestDTO requestDTO) {
        Ville ville = null;
        if (requestDTO.getVilleId() != null) {
            ville = villeRepository.findById(requestDTO.getVilleId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Ville introuvable avec l'ID : " + requestDTO.getVilleId()));
        }

        LieuHistorique lieu = LieuHistorique.builder()

                
                .epoque(requestDTO.getEpoque())
                .cordonnees(requestDTO.getCordonnees())
                
                
                
                
                
                .ville(ville)
                .build();

        LieuHistorique saved = lieuHistoriqueRepository.save(lieu);
        return mapToResponseDTO(saved);
    }

    @Override
    public LieuHistoriqueResponseDTO updateLieu(Long id, LieuHistoriqueRequestDTO requestDTO) {
        LieuHistorique lieu = findLieuOrThrow(id);

        if (requestDTO.getVilleId() != null) {
            Ville ville = villeRepository.findById(requestDTO.getVilleId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Ville introuvable avec l'ID : " + requestDTO.getVilleId()));
            lieu.setVille(ville);
        } else {
            lieu.setVille(null);
        }


        
        lieu.setEpoque(requestDTO.getEpoque());
        lieu.setCordonnees(requestDTO.getCordonnees());
        
        
        
        
        

        LieuHistorique updated = lieuHistoriqueRepository.save(lieu);
        return mapToResponseDTO(updated);
    }

    @Override
    public void deleteLieu(Long id) {
        LieuHistorique lieu = findLieuOrThrow(id);
        lieuHistoriqueRepository.delete(lieu);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LieuHistoriqueResponseDTO> searchLieux(String keyword) {
        return lieuHistoriqueRepository.findByNomHistoireContainingIgnoreCase(keyword)
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LieuHistoriqueResponseDTO> getLieuxWithPanorama360() {
        return lieuHistoriqueRepository.findAll()
                .stream()
                .filter(l -> "" != null && !"".trim().isEmpty())
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    private LieuHistorique findLieuOrThrow(Long id) {
        return lieuHistoriqueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lieu historique introuvable avec l'ID : " + id));
    }

    private LieuHistoriqueResponseDTO mapToResponseDTO(LieuHistorique lieu) {
        VilleSummaryDTO villeSummary = null;
        if (lieu.getVille() != null) {
            villeSummary = VilleSummaryDTO.builder()
                    .id(lieu.getVille().getIdVille())
                    .nom(lieu.getVille().getNomVille())
                    
                    
                    .build();
        }

        return LieuHistoriqueResponseDTO.builder()
                .idLieu(lieu.getIdLieu())

                
                .epoque(lieu.getEpoque())
                .cordonnees(lieu.getCordonnees())
                
                
                
                
                
                .ville(villeSummary)
                .build();
    }
}
