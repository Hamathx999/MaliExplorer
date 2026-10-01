package com.maliexplorer_backend.serviceimpl;

import com.maliexplorer_backend.dto.OpportuniteResponseDTO;
import com.maliexplorer_backend.exception.ResourceNotFoundException;
import com.maliexplorer_backend.model.*;
import com.maliexplorer_backend.repository.ArtisanRepository;
import com.maliexplorer_backend.repository.GuideRepository;
import com.maliexplorer_backend.repository.PromoteurRepository;
import com.maliexplorer_backend.service.OpportuniteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class OpportuniteServiceImpl implements OpportuniteService {

    private final ArtisanRepository artisanRepository;
    private final PromoteurRepository promoteurRepository;
    private final GuideRepository guideRepository;

    @Override
    @Transactional(readOnly = true)
    public List<OpportuniteResponseDTO> getOpportunitesValidees(String type) {
        List<OpportuniteResponseDTO> result = new ArrayList<>();

        boolean includeArtisans = (type == null || type.isBlank() || type.equalsIgnoreCase("ARTISAN"));
        boolean includePromoteurs = (type == null || type.isBlank() || type.equalsIgnoreCase("PROMOTEUR"));
        boolean includeGuides = (type == null || type.isBlank() || type.equalsIgnoreCase("GUIDE"));

        if (includeArtisans) {
            artisanRepository.findByRecherchePartenariatTrueAndStatutModeration(StatutModeration.VALIDE)
                    .forEach(a -> result.add(mapArtisanToDTO(a)));
        }

        if (includePromoteurs) {
            promoteurRepository.findByRecherchePartenariatTrueAndStatutModeration(StatutModeration.VALIDE)
                    .forEach(p -> result.add(mapPromoteurToDTO(p)));
        }

        if (includeGuides) {
            guideRepository.findByRecherchePartenariatTrueAndStatutModeration(StatutModeration.VALIDE)
                    .forEach(g -> result.add(mapGuideToDTO(g)));
        }

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<OpportuniteResponseDTO> getOpportunitesEnAttente() {
        List<OpportuniteResponseDTO> result = new ArrayList<>();

        artisanRepository.findByStatutModeration(StatutModeration.EN_ATTENTE_VALIDATION)
                .forEach(a -> result.add(mapArtisanToDTO(a)));

        promoteurRepository.findByStatutModeration(StatutModeration.EN_ATTENTE_VALIDATION)
                .forEach(p -> result.add(mapPromoteurToDTO(p)));

        guideRepository.findByStatutModeration(StatutModeration.EN_ATTENTE_VALIDATION)
                .forEach(g -> result.add(mapGuideToDTO(g)));

        return result;
    }

    @Override
    public OpportuniteResponseDTO validerOpportunite(int idUsers) {
        Optional<ArtisanModel> artisanOpt = artisanRepository.findById(idUsers);
        if (artisanOpt.isPresent()) {
            ArtisanModel artisan = artisanOpt.get();
            artisan.setStatutModeration(StatutModeration.VALIDE);
            artisanRepository.save(artisan);
            log.info("Opportunité artisan validée : ID={}", idUsers);
            return mapArtisanToDTO(artisan);
        }

        Optional<PromoteurModel> promoteurOpt = promoteurRepository.findById(idUsers);
        if (promoteurOpt.isPresent()) {
            PromoteurModel promoteur = promoteurOpt.get();
            promoteur.setStatutModeration(StatutModeration.VALIDE);
            promoteurRepository.save(promoteur);
            log.info("Opportunité promoteur validée : ID={}", idUsers);
            return mapPromoteurToDTO(promoteur);
        }

        Optional<GuideModel> guideOpt = guideRepository.findById(idUsers);
        if (guideOpt.isPresent()) {
            GuideModel guide = guideOpt.get();
            guide.setStatutModeration(StatutModeration.VALIDE);
            guideRepository.save(guide);
            log.info("Opportunité guide validée : ID={}", idUsers);
            return mapGuideToDTO(guide);
        }

        throw new ResourceNotFoundException("Partenaire introuvable avec l'ID utilisateur : " + idUsers);
    }

    @Override
    public OpportuniteResponseDTO rejeterOpportunite(int idUsers) {
        Optional<ArtisanModel> artisanOpt = artisanRepository.findById(idUsers);
        if (artisanOpt.isPresent()) {
            ArtisanModel artisan = artisanOpt.get();
            artisan.setStatutModeration(StatutModeration.REJETE);
            artisanRepository.save(artisan);
            log.info("Opportunité artisan rejetée : ID={}", idUsers);
            return mapArtisanToDTO(artisan);
        }

        Optional<PromoteurModel> promoteurOpt = promoteurRepository.findById(idUsers);
        if (promoteurOpt.isPresent()) {
            PromoteurModel promoteur = promoteurOpt.get();
            promoteur.setStatutModeration(StatutModeration.REJETE);
            promoteurRepository.save(promoteur);
            log.info("Opportunité promoteur rejetée : ID={}", idUsers);
            return mapPromoteurToDTO(promoteur);
        }

        Optional<GuideModel> guideOpt = guideRepository.findById(idUsers);
        if (guideOpt.isPresent()) {
            GuideModel guide = guideOpt.get();
            guide.setStatutModeration(StatutModeration.REJETE);
            guideRepository.save(guide);
            log.info("Opportunité guide rejetée : ID={}", idUsers);
            return mapGuideToDTO(guide);
        }

        throw new ResourceNotFoundException("Partenaire introuvable avec l'ID utilisateur : " + idUsers);
    }

    @Override
    public OpportuniteResponseDTO soumettreProjetPartenaire(int idUsers, String titreProjet, String besoinPartenariat, boolean recherchePartenariat) {
        Optional<ArtisanModel> artisanOpt = artisanRepository.findById(idUsers);
        if (artisanOpt.isPresent()) {
            ArtisanModel artisan = artisanOpt.get();
            artisan.setRecherchePartenariat(recherchePartenariat);
            artisan.setTitreProjet(titreProjet);
            artisan.setBesoinPartenariat(besoinPartenariat);
            artisan.setStatutModeration(recherchePartenariat ? StatutModeration.EN_ATTENTE_VALIDATION : StatutModeration.VALIDE);
            artisanRepository.save(artisan);
            return mapArtisanToDTO(artisan);
        }

        Optional<PromoteurModel> promoteurOpt = promoteurRepository.findById(idUsers);
        if (promoteurOpt.isPresent()) {
            PromoteurModel promoteur = promoteurOpt.get();
            promoteur.setRecherchePartenariat(recherchePartenariat);
            promoteur.setTitreProjet(titreProjet);
            promoteur.setBesoinPartenariat(besoinPartenariat);
            promoteur.setStatutModeration(recherchePartenariat ? StatutModeration.EN_ATTENTE_VALIDATION : StatutModeration.VALIDE);
            promoteurRepository.save(promoteur);
            return mapPromoteurToDTO(promoteur);
        }

        Optional<GuideModel> guideOpt = guideRepository.findById(idUsers);
        if (guideOpt.isPresent()) {
            GuideModel guide = guideOpt.get();
            guide.setRecherchePartenariat(recherchePartenariat);
            guide.setTitreProjet(titreProjet);
            guide.setBesoinPartenariat(besoinPartenariat);
            guide.setStatutModeration(recherchePartenariat ? StatutModeration.EN_ATTENTE_VALIDATION : StatutModeration.VALIDE);
            guideRepository.save(guide);
            return mapGuideToDTO(guide);
        }

        throw new ResourceNotFoundException("Partenaire introuvable avec l'ID utilisateur : " + idUsers);
    }

    @Override
    @Transactional(readOnly = true)
    public OpportuniteResponseDTO getMonStatutPartenaire(int idUsers) {
        Optional<ArtisanModel> artisanOpt = artisanRepository.findById(idUsers);
        if (artisanOpt.isPresent()) {
            return mapArtisanToDTO(artisanOpt.get());
        }

        Optional<PromoteurModel> promoteurOpt = promoteurRepository.findById(idUsers);
        if (promoteurOpt.isPresent()) {
            return mapPromoteurToDTO(promoteurOpt.get());
        }

        Optional<GuideModel> guideOpt = guideRepository.findById(idUsers);
        if (guideOpt.isPresent()) {
            return mapGuideToDTO(guideOpt.get());
        }

        throw new ResourceNotFoundException("Partenaire introuvable avec l'ID utilisateur : " + idUsers);
    }

    // ==================== MAPPERS PRIVÉS ====================

    private OpportuniteResponseDTO mapArtisanToDTO(ArtisanModel a) {
        return OpportuniteResponseDTO.builder()
                .idUsers(a.getIdUsers())
                .nomComplet(a.getPrenom() + " " + a.getNom())
                .email(a.getEmail())
                .adresse(a.getAdresse())
                .photoUrl(a.getPhotoUrl())
                .role(a.getRole())
                .typePartenaire("ARTISAN")
                .specialiteOuOrganisation(a.getTypeArtisanat())
                .recherchePartenariat(a.isRecherchePartenariat())
                .titreProjet(a.getTitreProjet())
                .besoinPartenariat(a.getBesoinPartenariat())
                .statutModeration(a.getStatutModeration())
                .dateCreation(a.getDateCreation())
                .build();
    }

    private OpportuniteResponseDTO mapPromoteurToDTO(PromoteurModel p) {
        return OpportuniteResponseDTO.builder()
                .idUsers(p.getIdUsers())
                .nomComplet(p.getPrenom() + " " + p.getNom())
                .email(p.getEmail())
                .adresse(p.getAdresse())
                .photoUrl(p.getPhotoUrl())
                .role(p.getRole())
                .typePartenaire("PROMOTEUR")
                .specialiteOuOrganisation(p.getNomOrganisation())
                .recherchePartenariat(p.isRecherchePartenariat())
                .titreProjet(p.getTitreProjet())
                .besoinPartenariat(p.getBesoinPartenariat())
                .statutModeration(p.getStatutModeration())
                .dateCreation(p.getDateCreation())
                .build();
    }

    private OpportuniteResponseDTO mapGuideToDTO(GuideModel g) {
        return OpportuniteResponseDTO.builder()
                .idUsers(g.getIdUsers())
                .nomComplet(g.getPrenom() + " " + g.getNom())
                .email(g.getEmail())
                .adresse(g.getAdresse())
                .photoUrl(g.getPhotoUrl())
                .role(g.getRole())
                .typePartenaire("GUIDE")
                .specialiteOuOrganisation("Langue : " + g.getLangue())
                .recherchePartenariat(g.isRecherchePartenariat())
                .titreProjet(g.getTitreProjet())
                .besoinPartenariat(g.getBesoinPartenariat())
                .statutModeration(g.getStatutModeration())
                .dateCreation(g.getDateCreation())
                .build();
    }
}
