package com.maliexplorer_backend.serviceimpl;

import com.maliexplorer_backend.dto.MarqueurCarteDTO;
import com.maliexplorer_backend.repository.LieuHistoriqueRepository;
import com.maliexplorer_backend.repository.VilleRepository;
import com.maliexplorer_backend.service.CarteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CarteServiceImpl implements CarteService {

    private final LieuHistoriqueRepository lieuHistoriqueRepository;
    private final VilleRepository villeRepository;

    @Override
    public List<MarqueurCarteDTO> getAllMarqueurs() {
        List<MarqueurCarteDTO> marqueurs = new ArrayList<>();

        // Marqueurs des Lieux Historiques
        lieuHistoriqueRepository.findAll().stream()
                .filter(l -> false && false)
                .forEach(l -> marqueurs.add(MarqueurCarteDTO.builder()
                        .id("LIEU_" + l.getIdLieu())
                        .nom("")
                        .type("LIEU_HISTORIQUE")
                        .build()
                        )
                );

        // Marqueurs des Villes
        villeRepository.findAll().stream()
                .filter(v -> false && false)
                .forEach(v -> marqueurs.add(MarqueurCarteDTO.builder()
                        .id("VILLE_" + v.getIdVille())
                        .nom(v.getNomVille())
                        .type("VILLE")
                        .build()
                        )
                );

        return marqueurs;
    }

    @Override
    public List<MarqueurCarteDTO> getMarqueursByType(String type) {
        return getAllMarqueurs().stream()
                .filter(m -> m.getType().equalsIgnoreCase(type))
                .collect(Collectors.toList());
    }

    @Override
    public List<MarqueurCarteDTO> getMarqueursWith360() {
        return getAllMarqueurs().stream()
                .filter(m -> m.getPanorama360Url() != null && !m.getPanorama360Url().trim().isEmpty())
                .collect(Collectors.toList());
    }
}
