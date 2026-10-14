package com.maliexplorer_backend.serviceimpl;

import com.maliexplorer_backend.dto.SearchResultItemDTO;
import com.maliexplorer_backend.model.*;
import com.maliexplorer_backend.repository.*;
import com.maliexplorer_backend.service.RechercheGlobaleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RechercheGlobaleServiceImpl implements RechercheGlobaleService {

    private final VilleRepository villeRepository;
    private final RegionRepository regionRepository;
    private final LieuHistoriqueRepository lieuHistoriqueRepository;
    private final PlatRepository platRepository;
    private final EthnieRepository ethnieRepository;
    private final PresidentRepository presidentRepository;
    private final ReligionRepository religionRepository;
    private final ArtisanRepository artisanRepository;
    private final GuideRepository guideRepository;
    private final EvenementRepository evenementRepository;

    @Override
    @Transactional(readOnly = true)
    public List<SearchResultItemDTO> rechercher(String query) {
        if (query == null || query.trim().length() < 2) {
            return List.of();
        }

        String rawQuery = query.trim();
        String normalizedQuery = normalize(rawQuery);
        List<SearchResultItemDTO> results = new ArrayList<>();

        // 1. Villes
        try {
            List<VilleModel> villes = villeRepository.findByNomVilleContainingIgnoreCase(rawQuery);
            for (VilleModel v : villes) {
                results.add(SearchResultItemDTO.builder()
                        .id("ville-" + v.getIdVille())
                        .titre(v.getNomVille())
                        .sousTitre(v.getRegion() != null ? "Région de " + v.getRegion() : "Ville du Mali")
                        .categorie("Villes")
                        .description(v.getDescription())
                        .imageUrl(v.getImageUrl())
                        .route("/city-detail")
                        .payload(v.getIdVille())
                        .build());
            }
        } catch (Exception ignored) {}

        // 2. Régions
        try {
            List<RegionModel> regions = regionRepository.findByNomRegionContainingIgnoreCase(rawQuery);
            for (RegionModel r : regions) {
                results.add(SearchResultItemDTO.builder()
                        .id("region-" + r.getIdRegion())
                        .titre(r.getNomRegion())
                        .sousTitre(r.getChefLieu() != null ? "Chef-lieu: " + r.getChefLieu() : "Région administrative")
                        .categorie("Régions")
                        .description(r.getDescription())
                        .imageUrl(r.getImageUrl())
                        .route("/region-detail")
                        .payload(r.getIdRegion())
                        .build());
            }
        } catch (Exception ignored) {}

        // 3. Lieux Historiques
        try {
            List<LieuHistoriqueModel> lieux = lieuHistoriqueRepository.findByNomLieuContainingIgnoreCase(rawQuery);
            for (LieuHistoriqueModel l : lieux) {
                results.add(SearchResultItemDTO.builder()
                        .id("lieu-" + l.getIdLieu())
                        .titre(l.getNomLieu())
                        .sousTitre(l.getVille() != null ? l.getVille().getNomVille() : "Site historique")
                        .categorie("Lieux Historiques")
                        .description(l.getDescription())
                        .imageUrl(l.getImageUrl())
                        .route("/visite-virtuelle-360")
                        .payload(l.getIdLieu())
                        .build());
            }
        } catch (Exception ignored) {}

        // 4. Plats & Gastronomie
        try {
            List<PlatModel> plats = platRepository.findByNomPlatContainingIgnoreCase(rawQuery);
            if (plats.isEmpty() && (normalizedQuery.contains("saga") || normalizedQuery.contains("saka"))) {
                List<PlatModel> sagaPlats = platRepository.findByNomPlatContainingIgnoreCase("saka");
                if (sagaPlats.isEmpty()) {
                    sagaPlats = platRepository.findByNomPlatContainingIgnoreCase("saga");
                }
                plats = sagaPlats;
            }
            for (PlatModel p : plats) {
                results.add(SearchResultItemDTO.builder()
                        .id("plat-" + p.getIdPlat())
                        .titre(p.getNomPlat())
                        .sousTitre("Spécialité culinaire malienne")
                        .categorie("Gastronomie")
                        .description(p.getDescription())
                        .imageUrl(p.getImageUrl())
                        .route("/dish-detail")
                        .payload(p.getIdPlat())
                        .build());
            }
        } catch (Exception ignored) {}

        // 5. Ethnies & Cultures
        try {
            List<EthnieModel> ethnies = ethnieRepository.findByNomEthnieContainingIgnoreCase(rawQuery);
            for (EthnieModel e : ethnies) {
                results.add(SearchResultItemDTO.builder()
                        .id("ethnie-" + e.getIdEthnie())
                        .titre(e.getNomEthnie())
                        .sousTitre(e.getLangues() != null ? "Langues: " + e.getLangues() : "Peuple du Mali")
                        .categorie("Ethnies")
                        .description(e.getDescription())
                        .imageUrl(e.getImageUrl())
                        .route("/ethnicity-detail")
                        .payload(e.getIdEthnie())
                        .build());
            }
        } catch (Exception ignored) {}

        // 6. Religions & Pratiques spirituelles
        try {
            List<ReligionModel> religions = religionRepository.searchReligions(rawQuery);
            for (ReligionModel rel : religions) {
                results.add(SearchResultItemDTO.builder()
                        .id("religion-" + rel.getId())
                        .titre(rel.getNom())
                        .sousTitre(rel.getProportionPopulation() != null ? "Proportion: " + rel.getProportionPopulation() : (rel.getCategorie() != null ? rel.getCategorie() : "Tradition spirituelle"))
                        .categorie("Religions")
                        .description(rel.getDescription())
                        .imageUrl(rel.getImageUrl())
                        .route("/religion-detail")
                        .payload(rel.getId())
                        .build());
            }
        } catch (Exception ignored) {}

        // 7. Présidents / Chefs d'État
        try {
            List<PresidentModel> presidents = presidentRepository.findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCase(rawQuery, rawQuery);
            for (PresidentModel pres : presidents) {
                String full = (pres.getPrenom() != null ? pres.getPrenom() + " " : "") + (pres.getNom() != null ? pres.getNom() : "");
                results.add(SearchResultItemDTO.builder()
                        .id("president-" + pres.getIdPresident())
                        .titre(full.trim())
                        .sousTitre(pres.getPeriodeMandat() != null ? "Mandat: " + pres.getPeriodeMandat() : "Chef d'État")
                        .categorie("Chefs d'État")
                        .description(pres.getBiographie())
                        .imageUrl(pres.getPhotoUrl())
                        .route("/president-detail")
                        .payload(pres.getIdPresident())
                        .build());
            }
        } catch (Exception ignored) {}

        // 8. Artisans
        try {
            List<ArtisanModel> artisans = artisanRepository.findAll();
            for (ArtisanModel a : artisans) {
                String nomArtisan = (a.getPrenom() != null ? a.getPrenom() + " " : "") + (a.getNom() != null ? a.getNom() : "");
                if (nomArtisan.trim().isEmpty()) {
                    nomArtisan = a.getTitreProjet();
                }
                if (nomArtisan != null && normalize(nomArtisan).contains(normalizedQuery)) {
                    results.add(SearchResultItemDTO.builder()
                            .id("artisan-" + a.getIdUsers())
                            .titre(nomArtisan.trim())
                            .sousTitre(a.getTypeArtisanat() != null ? a.getTypeArtisanat() : "Artisan d'art malien")
                            .categorie("Artisans")
                            .description(a.getBesoinPartenariat())
                            .imageUrl(a.getPhotoUrl())
                            .route("/artisans")
                            .payload(a.getIdUsers())
                            .build());
                }
            }
        } catch (Exception ignored) {}

        // 9. Événements
        try {
            List<EvenementModel> evts = evenementRepository.findByTitreContainingIgnoreCase(rawQuery);
            for (EvenementModel evt : evts) {
                results.add(SearchResultItemDTO.builder()
                        .id("evenement-" + evt.getId())
                        .titre(evt.getTitre())
                        .sousTitre(evt.getLieu() != null ? evt.getLieu() : "Événement culturel")
                        .categorie("Événements")
                        .description(evt.getDescription())
                        .imageUrl(evt.getImageUrl())
                        .route("/explorer")
                        .payload(evt.getId())
                        .build());
            }
        } catch (Exception ignored) {}

        return results;
    }

    private String normalize(String input) {
        if (input == null) return "";
        return Normalizer.normalize(input, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .toLowerCase();
    }
}
