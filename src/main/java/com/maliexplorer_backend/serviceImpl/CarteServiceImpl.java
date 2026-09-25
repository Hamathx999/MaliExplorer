package com.maliexplorer_backend.serviceImpl;

import com.maliexplorer_backend.dto.MarqueurCarteDTO;
import com.maliexplorer_backend.repository.LieuHistoriqueRepository;
import com.maliexplorer_backend.repository.PointInteretRepository;
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
    private final PointInteretRepository pointInteretRepository;

    @Override
    public List<MarqueurCarteDTO> getAllMarqueurs() {
        List<MarqueurCarteDTO> marqueurs = new ArrayList<>();

        // Marqueurs des Lieux Historiques
        lieuHistoriqueRepository.findAll().stream()
                .filter(l -> l.getLatitude() != null && l.getLongitude() != null)
                .forEach(l -> marqueurs.add(MarqueurCarteDTO.builder()
                        .id("LIEU_" + l.getIdLieu())
                        .nom(l.getNomHistoire())
                        .type("LIEU_HISTORIQUE")
                        .latitude(l.getLatitude())
                        .longitude(l.getLongitude())
                        .description(l.getDescription())
                        .imageUrl(l.getImageUrl())
                        .panorama360Url(l.getPanorama360Url())
                        .referenceId(l.getIdLieu())
                        .categorie("Histoire & Patrimoine")
                        .build()));

        // Marqueurs des Villes
        villeRepository.findAll().stream()
                .filter(v -> v.getLatitude() != null && v.getLongitude() != null)
                .forEach(v -> marqueurs.add(MarqueurCarteDTO.builder()
                        .id("VILLE_" + v.getIdVille())
                        .nom(v.getNomVille())
                        .type("VILLE")
                        .latitude(v.getLatitude())
                        .longitude(v.getLongitude())
                        .description(v.getDescription())
                        .imageUrl(v.getImageUrl())
                        .referenceId(v.getIdVille())
                        .categorie(Boolean.TRUE.equals(v.getEstCapitale()) ? "Capitale" : "Ville")
                        .build()));

        // Marqueurs des Points d'intérêt (POIs touristiques, etc.)
        pointInteretRepository.findAll().stream()
                .filter(p -> p.getLatitude() != null && p.getLongitude() != null)
                .forEach(p -> marqueurs.add(MarqueurCarteDTO.builder()
                        .id("POI_" + p.getId())
                        .nom(p.getNom())
                        .type("POINT_INTERET")
                        .latitude(p.getLatitude())
                        .longitude(p.getLongitude())
                        .description(p.getDescription())
                        .imageUrl(p.getImageUrl())
                        .panorama360Url(p.getPanorama360Url())
                        .referenceId(p.getId())
                        .categorie(p.getType())
                        .build()));

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
