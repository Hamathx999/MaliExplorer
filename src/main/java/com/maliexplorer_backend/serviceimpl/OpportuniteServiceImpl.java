package com.maliexplorer_backend.serviceimpl;

import com.maliexplorer_backend.config.SecurityUtils;
import com.maliexplorer_backend.dto.OpportuniteResponseDTO;
import com.maliexplorer_backend.exception.ResourceNotFoundException;
import com.maliexplorer_backend.model.*;
import com.maliexplorer_backend.repository.GuideRepository;
import com.maliexplorer_backend.repository.PromoteurRepository;
import com.maliexplorer_backend.repository.utilisateurRepository;
import com.maliexplorer_backend.service.OpportuniteService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class OpportuniteServiceImpl implements OpportuniteService {

    private final PromoteurRepository promoteurRepository;
    private final GuideRepository guideRepository;
    private final utilisateurRepository userRepository;

    @PersistenceContext
    private final EntityManager entityManager;

    @Override
    @Transactional(readOnly = true)
    public List<OpportuniteResponseDTO> getOpportunitesValidees(String type, String keyword) {
        List<OpportuniteResponseDTO> result = new ArrayList<>();

        boolean includePromoteurs = (type == null || type.isBlank() || type.equalsIgnoreCase("PROMOTEUR"));
        boolean includeGuides = (type == null || type.isBlank() || type.equalsIgnoreCase("GUIDE"));

        if (includePromoteurs) {
            promoteurRepository.findByRecherchePartenariatTrueAndStatutModeration(StatutModeration.VALIDE)
                    .forEach(p -> result.add(mapPromoteurToDTO(p)));
        }

        if (includeGuides) {
            guideRepository.findByRecherchePartenariatTrueAndStatutModeration(StatutModeration.VALIDE)
                    .forEach(g -> result.add(mapGuideToDTO(g)));
        }

        if (keyword != null && !keyword.isBlank()) {
            String kw = keyword.trim().toLowerCase();
            return result.stream()
                    .filter(o -> (o.getNomComplet() != null && o.getNomComplet().toLowerCase().contains(kw))
                            || (o.getTitreProjet() != null && o.getTitreProjet().toLowerCase().contains(kw))
                            || (o.getBesoinPartenariat() != null && o.getBesoinPartenariat().toLowerCase().contains(kw))
                            || (o.getSpecialiteOuOrganisation() != null && o.getSpecialiteOuOrganisation().toLowerCase().contains(kw))
                            || (o.getAdresse() != null && o.getAdresse().toLowerCase().contains(kw)))
                    .collect(Collectors.toList());
        }

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<OpportuniteResponseDTO> getOpportunitesParStatut(StatutModeration statut) {
        StatutModeration targetStatut = (statut != null) ? statut : StatutModeration.EN_ATTENTE_VALIDATION;
        List<OpportuniteResponseDTO> result = new ArrayList<>();

        promoteurRepository.findByStatutModeration(targetStatut)
                .forEach(p -> result.add(mapPromoteurToDTO(p)));

        guideRepository.findByStatutModeration(targetStatut)
                .forEach(g -> result.add(mapGuideToDTO(g)));

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<OpportuniteResponseDTO> getOpportunitesEnAttente() {
        return getOpportunitesParStatut(StatutModeration.EN_ATTENTE_VALIDATION);
    }

    private void ensurePromoteurNomOrganisation(PromoteurModel promoteur) {
        String nomOrg = promoteur.getNomOrganisation();

        // 1. Si null ou vide, vérifier dans userRepository / utilisateurModel
        if ((nomOrg == null || nomOrg.isBlank()) && userRepository != null) {
            Optional<utilisateurModel> uOpt = userRepository.findById(promoteur.getIdUsers());
            if (uOpt.isPresent() && uOpt.get().getNomOrganisation() != null && !uOpt.get().getNomOrganisation().isBlank()) {
                nomOrg = uOpt.get().getNomOrganisation().trim();
            }
        }

        // 2. Si toujours vide, vérifier le titre de projet
        if ((nomOrg == null || nomOrg.isBlank()) && promoteur.getTitreProjet() != null && !promoteur.getTitreProjet().isBlank()) {
            String titre = promoteur.getTitreProjet().trim();
            if (titre.startsWith("Demande d'adhésion :")) {
                nomOrg = titre.substring("Demande d'adhésion :".length()).trim();
            } else {
                nomOrg = titre;
            }
        }

        // 3. Fallback sur nom de l'utilisateur ou valeur par défaut
        if (nomOrg == null || nomOrg.isBlank()) {
            String fullName = ((promoteur.getPrenom() != null ? promoteur.getPrenom() + " " : "") +
                               (promoteur.getNom() != null ? promoteur.getNom() : "")).trim();
            nomOrg = !fullName.isBlank() ? "Organisation " + fullName : "Organisation Partenaire";
        }

        promoteur.setNomOrganisation(nomOrg);
    }

    @Override
    public OpportuniteResponseDTO validerOpportunite(int idUsers) {
        Optional<PromoteurModel> promoteurOpt = promoteurRepository.findById(idUsers);
        if (promoteurOpt.isPresent()) {
            PromoteurModel promoteur = promoteurOpt.get();
            ensurePromoteurNomOrganisation(promoteur);
            promoteur.setStatutModeration(StatutModeration.VALIDE);
            promoteur.setMotifRejet(null);
            promoteur.setStatut("ACTIF");
            promoteur = promoteurRepository.save(promoteur);
            final String finalOrg = promoteur.getNomOrganisation();
            userRepository.findById(idUsers).ifPresent(u -> {
                u.setStatut("ACTIF");
                if (finalOrg != null && !finalOrg.isBlank()) {
                    u.setNomOrganisation(finalOrg);
                }
                userRepository.save(u);
            });
            log.info("Opportunité promoteur validée : ID={}", idUsers);
            return mapPromoteurToDTO(promoteur);
        }

        Optional<GuideModel> guideOpt = guideRepository.findById(idUsers);
        if (guideOpt.isPresent()) {
            GuideModel guide = guideOpt.get();
            guide.setStatutModeration(StatutModeration.VALIDE);
            guide.setMotifRejet(null);
            guide.setStatut("ACTIF");
            guideRepository.save(guide);
            userRepository.findById(idUsers).ifPresent(u -> {
                u.setStatut("ACTIF");
                userRepository.save(u);
            });
            log.info("Opportunité guide validée : ID={}", idUsers);
            return mapGuideToDTO(guide);
        }

        // Si l'utilisateur existe dans utilisateurs mais n'a pas encore de fiche promoteurs ou guides
        Optional<utilisateurModel> userOpt = userRepository.findById(idUsers);
        if (userOpt.isPresent()) {
            utilisateurModel user = userOpt.get();
            user.setStatut("ACTIF");
            userRepository.save(user);

            if (user.getRole() == RoleModel.promoteur) {
                String org = user.getNomOrganisation() != null && !user.getNomOrganisation().isBlank()
                        ? user.getNomOrganisation() : "Organisation " + user.getNom();
                entityManager.createNativeQuery(
                        "INSERT INTO promoteurs (id_users, nom_organisation, piece_identite, recherche_partenariat, statut_moderation) " +
                        "VALUES (:id, :org, :piece, 1, 'VALIDE') " +
                        "ON DUPLICATE KEY UPDATE statut_moderation = 'VALIDE', motif_rejet = NULL")
                        .setParameter("id", idUsers)
                        .setParameter("org", org)
                        .setParameter("piece", user.getPieceIdentite() != null ? user.getPieceIdentite() : "")
                        .executeUpdate();
                return promoteurRepository.findById(idUsers).map(this::mapPromoteurToDTO).orElse(null);
            } else {
                entityManager.createNativeQuery(
                        "INSERT INTO guides (id_users, piece_identite, recherche_partenariat, experience, langue, statut_moderation) " +
                        "VALUES (:id, :piece, 0, 1, 'Français, Bambara', 'VALIDE') " +
                        "ON DUPLICATE KEY UPDATE statut_moderation = 'VALIDE', motif_rejet = NULL")
                        .setParameter("id", idUsers)
                        .setParameter("piece", user.getPieceIdentite() != null ? user.getPieceIdentite() : "")
                        .executeUpdate();
                return guideRepository.findById(idUsers).map(this::mapGuideToDTO).orElse(null);
            }
        }

        throw new ResourceNotFoundException("Partenaire introuvable avec l'ID utilisateur : " + idUsers);
    }

    @Override
    public OpportuniteResponseDTO rejeterOpportunite(int idUsers, String motifRejet) {
        Optional<PromoteurModel> promoteurOpt = promoteurRepository.findById(idUsers);
        if (promoteurOpt.isPresent()) {
            PromoteurModel promoteur = promoteurOpt.get();
            ensurePromoteurNomOrganisation(promoteur);
            promoteur.setStatutModeration(StatutModeration.REJETE);
            promoteur.setMotifRejet(motifRejet);
            promoteur = promoteurRepository.save(promoteur);
            final String finalOrg = promoteur.getNomOrganisation();
            userRepository.findById(idUsers).ifPresent(u -> {
                if (finalOrg != null && !finalOrg.isBlank()) {
                    u.setNomOrganisation(finalOrg);
                    userRepository.save(u);
                }
            });
            log.info("Opportunité promoteur rejetée : ID={}, motif={}", idUsers, motifRejet);
            return mapPromoteurToDTO(promoteur);
        }

        Optional<GuideModel> guideOpt = guideRepository.findById(idUsers);
        if (guideOpt.isPresent()) {
            GuideModel guide = guideOpt.get();
            guide.setStatutModeration(StatutModeration.REJETE);
            guide.setMotifRejet(motifRejet);
            guideRepository.save(guide);
            log.info("Opportunité guide rejetée : ID={}, motif={}", idUsers, motifRejet);
            return mapGuideToDTO(guide);
        }

        // Si l'utilisateur existe dans utilisateurs mais n'a pas encore de fiche promoteurs ou guides
        Optional<utilisateurModel> userOpt = userRepository.findById(idUsers);
        if (userOpt.isPresent()) {
            utilisateurModel user = userOpt.get();
            if (user.getRole() == RoleModel.promoteur) {
                String org = user.getNomOrganisation() != null && !user.getNomOrganisation().isBlank()
                        ? user.getNomOrganisation() : "Organisation " + user.getNom();
                entityManager.createNativeQuery(
                        "INSERT INTO promoteurs (id_users, nom_organisation, piece_identite, recherche_partenariat, statut_moderation, motif_rejet) " +
                        "VALUES (:id, :org, :piece, 0, 'REJETE', :motif) " +
                        "ON DUPLICATE KEY UPDATE statut_moderation = 'REJETE', motif_rejet = :motif")
                        .setParameter("id", idUsers)
                        .setParameter("org", org)
                        .setParameter("piece", user.getPieceIdentite() != null ? user.getPieceIdentite() : "")
                        .setParameter("motif", motifRejet)
                        .executeUpdate();
                return promoteurRepository.findById(idUsers).map(this::mapPromoteurToDTO).orElse(null);
            } else {
                entityManager.createNativeQuery(
                        "INSERT INTO guides (id_users, piece_identite, recherche_partenariat, experience, langue, statut_moderation, motif_rejet) " +
                        "VALUES (:id, :piece, 0, 1, 'Français, Bambara', 'REJETE', :motif) " +
                        "ON DUPLICATE KEY UPDATE statut_moderation = 'REJETE', motif_rejet = :motif")
                        .setParameter("id", idUsers)
                        .setParameter("piece", user.getPieceIdentite() != null ? user.getPieceIdentite() : "")
                        .setParameter("motif", motifRejet)
                        .executeUpdate();
                return guideRepository.findById(idUsers).map(this::mapGuideToDTO).orElse(null);
            }
        }

        throw new ResourceNotFoundException("Partenaire introuvable avec l'ID utilisateur : " + idUsers);
    }

    @Override
    public OpportuniteResponseDTO soumettreProjetPartenaire(int idUsers, String titreProjet, String besoinPartenariat, boolean recherchePartenariat) {
        RoleModel currentRole = userRepository != null
                ? userRepository.findById(idUsers).map(utilisateurModel::getRole).orElse(null)
                : null;

        if (currentRole == RoleModel.guide) {
            Optional<GuideModel> guideOpt = guideRepository.findById(idUsers);
            if (guideOpt.isPresent()) {
                return updateGuideProjet(guideOpt.get(), titreProjet, besoinPartenariat, recherchePartenariat);
            }
        }

        Optional<PromoteurModel> promoteurOpt = promoteurRepository.findById(idUsers);
        if (promoteurOpt.isPresent()) {
            PromoteurModel promoteur = promoteurOpt.get();
            ensurePromoteurNomOrganisation(promoteur);
            promoteur.setRecherchePartenariat(recherchePartenariat);
            promoteur.setTitreProjet(titreProjet);
            promoteur.setBesoinPartenariat(besoinPartenariat);
            if (promoteur.getStatutModeration() != StatutModeration.VALIDE) {
                promoteur.setStatutModeration(recherchePartenariat ? StatutModeration.EN_ATTENTE_VALIDATION : StatutModeration.VALIDE);
            }
            promoteur.setMotifRejet(null);
            promoteurRepository.save(promoteur);
            return mapPromoteurToDTO(promoteur);
        }

        Optional<GuideModel> guideOpt = guideRepository.findById(idUsers);
        if (guideOpt.isPresent()) {
            return updateGuideProjet(guideOpt.get(), titreProjet, besoinPartenariat, recherchePartenariat);
        }

        throw new ResourceNotFoundException("Partenaire introuvable avec l'ID utilisateur : " + idUsers);
    }

    private OpportuniteResponseDTO updateGuideProjet(GuideModel guide, String titreProjet, String besoinPartenariat, boolean recherchePartenariat) {
        guide.setRecherchePartenariat(recherchePartenariat);
        guide.setTitreProjet(titreProjet);
        guide.setBesoinPartenariat(besoinPartenariat);
        if (besoinPartenariat != null && !besoinPartenariat.isBlank()) {
            guide.setDescription(besoinPartenariat);
        }
        if (guide.getStatutModeration() != StatutModeration.VALIDE) {
            guide.setStatutModeration(recherchePartenariat ? StatutModeration.EN_ATTENTE_VALIDATION : StatutModeration.VALIDE);
        }
        guide.setMotifRejet(null);
        guideRepository.save(guide);
        return mapGuideToDTO(guide);
    }

    @Override
    @Transactional(readOnly = true)
    public OpportuniteResponseDTO getMonStatutPartenaire(int idUsers) {
        RoleModel currentRole = userRepository != null
                ? userRepository.findById(idUsers).map(utilisateurModel::getRole).orElse(null)
                : null;

        if (currentRole == RoleModel.guide) {
            Optional<GuideModel> guideOpt = guideRepository.findById(idUsers);
            if (guideOpt.isPresent()) {
                return mapGuideToDTO(guideOpt.get());
            }
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

    private OpportuniteResponseDTO mapPromoteurToDTO(PromoteurModel p) {
        boolean isPrivileged = SecurityUtils.isAdmin() || SecurityUtils.isCurrentUserOrAdmin(p.getIdUsers());

        String telephone = p.getTelephone();
        String nomOrg = p.getNomOrganisation();
        if ((telephone == null || telephone.isBlank() || nomOrg == null || nomOrg.isBlank()) && userRepository != null) {
            Optional<utilisateurModel> uOpt = userRepository.findById(p.getIdUsers());
            if (uOpt.isPresent()) {
                utilisateurModel u = uOpt.get();
                if (telephone == null || telephone.isBlank()) {
                    telephone = u.getTelephone();
                }
                if (nomOrg == null || nomOrg.isBlank()) {
                    nomOrg = u.getNomOrganisation();
                }
            }
        }

        if (nomOrg == null || nomOrg.isBlank()) {
            nomOrg = (p.getNom() != null && !p.getNom().isBlank()) ? "Organisation " + p.getNom() : "Organisation Partenaire";
        }

        return OpportuniteResponseDTO.builder()
                .idUsers(p.getIdUsers())
                .nomComplet(p.getPrenom() + " " + p.getNom())
                .photoUrl(p.getPhotoUrl())
                .role(p.getRole())
                .typePartenaire("PROMOTEUR")
                .nomOrganisation(nomOrg)
                .specialiteOuOrganisation(nomOrg)
                .recherchePartenariat(p.isRecherchePartenariat())
                .titreProjet(p.getTitreProjet())
                .besoinPartenariat(p.getBesoinPartenariat())
                .statutModeration(p.getStatutModeration())
                .motifRejet(p.getMotifRejet())
                .dateCreation(p.getDateCreation())
                .email(isPrivileged ? p.getEmail() : null)
                .adresse(isPrivileged ? p.getAdresse() : null)
                .pieceIdentite(isPrivileged ? p.getPieceIdentite() : null)
                .telephone(isPrivileged ? telephone : null)
                .experience(null)
                .langue(null)
                .build();
    }

    private OpportuniteResponseDTO mapGuideToDTO(GuideModel g) {
        boolean isPrivileged = SecurityUtils.isAdmin() || SecurityUtils.isCurrentUserOrAdmin(g.getIdUsers());

        String telephone = g.getTelephone();
        if ((telephone == null || telephone.isBlank()) && userRepository != null) {
            telephone = userRepository.findById(g.getIdUsers()).map(utilisateurModel::getTelephone).orElse(null);
        }

        return OpportuniteResponseDTO.builder()
                .idUsers(g.getIdUsers())
                .nomComplet(g.getPrenom() + " " + g.getNom())
                .photoUrl(g.getPhotoUrl())
                .role(g.getRole())
                .typePartenaire("GUIDE")
                .specialiteOuOrganisation("Langue : " + g.getLangue())
                .nomOrganisation(g.getNomOrganisation() != null && !g.getNomOrganisation().isBlank() ? g.getNomOrganisation() : null)
                .langue(g.getLangue())
                .experience(g.getExperience())
                .recherchePartenariat(g.isRecherchePartenariat())
                .titreProjet(g.getTitreProjet())
                .besoinPartenariat(g.getBesoinPartenariat())
                .statutModeration(g.getStatutModeration())
                .motifRejet(g.getMotifRejet())
                .dateCreation(g.getDateCreation())
                .email(isPrivileged ? g.getEmail() : null)
                .adresse(isPrivileged ? g.getAdresse() : null)
                .pieceIdentite(isPrivileged ? g.getPieceIdentite() : null)
                .telephone(isPrivileged ? telephone : null)
                .build();
    }
}
